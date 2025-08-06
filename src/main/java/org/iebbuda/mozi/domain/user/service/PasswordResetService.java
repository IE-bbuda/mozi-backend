package org.iebbuda.mozi.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.user.domain.PasswordResetSessionVO;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.iebbuda.mozi.domain.user.dto.request.AccountVerificationRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.PasswordResetRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.EmailCodeVerifyRequestDTO;
import org.iebbuda.mozi.domain.user.mapper.PasswordResetSessionMapper;
import org.iebbuda.mozi.domain.user.mapper.UserMapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserMapper userMapper;  // 기존 사용자 매퍼 활용
    private final PasswordResetSessionMapper sessionMapper;  // 세션 전용 매퍼
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    // 세션 유효 시간 (분)
    private static final int SESSION_EXPIRY_MINUTES = 30;


    /**
     * 1단계: 이메일 인증번호 발송
     * @param email 인증번호를 받을 이메일 주소
     */
    @Transactional
    public void sendEmailVerificationCode(String email) {
        log.info("이메일 인증번호 발송 시작 - 이메일: {}", email);

        // 이메일 인증번호 발송 (계정 존재 여부는 확인하지 않음 - 보안상 이유)
        emailVerificationService.sendVerificationCode(email);

        log.info("이메일 인증번호 발송 완료 - 이메일: {}", email);
    }

    /**
     * 2단계: 이메일 인증번호 확인
     * @param request 이메일과 인증번호
     */
    @Transactional
    public void verifyEmailCode(EmailCodeVerifyRequestDTO request) {
        log.info("이메일 인증번호 확인 시작 - 이메일: {}", request.getEmail());

        if (!emailVerificationService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            log.warn("이메일 인증번호 확인 실패 - 이메일: {}", request.getEmail());
            throw new BaseException(BaseResponseStatus.INVALID_VERIFICATION_CODE);
        }

        log.info("이메일 인증번호 확인 완료 - 이메일: {}", request.getEmail());
    }

    /**
     * 3단계: 계정 확인 및 토큰 발급
     * @param request 로그인ID와 이메일
     * @return 비밀번호 재설정용 토큰
     */
    @Transactional
    public String verifyAccountAndIssueToken(AccountVerificationRequestDTO request) {
        log.info("계정 확인 및 토큰 발급 시작 - 로그인ID: {}, 이메일: {}",
                request.getLoginId(), request.getEmail());

        // 1. 사용자 조회 및 검증
        UserVO user = findAndValidateUser(request.getLoginId(), request.getEmail());

        validateNotOAuthUser(user, "비밀번호 리셋");

        // 2. 이메일 인증 여부 확인 (이 부분이 빠져있음!)
        if (!emailVerificationService.isEmailVerified(request.getEmail())) {
            log.warn("이메일 인증 미완료로 계정 확인 시도: {}", request.getEmail());
            throw new BaseException(BaseResponseStatus.EMAIL_NOT_VERIFIED);
        }

        // 3. 기존 세션 정리
        cleanupExistingSessions(user.getUserId());

        // 4. 새 세션 생성 및 토큰 발급
        String token = createPasswordResetSession(user);

        log.info("토큰 발급 완료 - 사용자ID: {}, 토큰: {}",
                user.getUserId(), maskToken(token));

        return token;
    }

    /**
     * 4단계: 비밀번호 재설정 (기존 메서드 그대로)
     * @param request 토큰과 새 비밀번호
     */
    @Transactional
    public void resetPassword(PasswordResetRequestDTO request) {
        log.info("비밀번호 재설정 시작 - 토큰: {}", maskToken(request.getToken()));

        // 1. 세션 유효성 검사
        PasswordResetSessionVO session = validateResetSession(request.getToken());

        // 2. 비밀번호 업데이트
        updateUserPassword(session.getUserId(), request.getNewPassword(), request.getToken());

        // 3. 세션 완료 처리
        completeResetSession(request.getToken());
        // 3. 기존 세션 정리
        cleanupExistingSessions(session.getUserId());
        log.info("비밀번호 재설정 완료 - 사용자ID: {}, 세션ID: {}", session.getUserId(), session.getId());
    }


    /**
     * 사용자 조회 및 검증
     */
    private UserVO findAndValidateUser(String loginId, String email) {
        UserVO user = userMapper.findByLoginIdAndEmail(loginId, email);

        if (user == null) {
            log.warn("일치하는 계정 없음 - 로그인ID: {}, 이메일: {}", loginId, email);
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND_FOR_RESET);
        }

        log.debug("사용자 조회 성공 - 사용자ID: {}", user.getUserId());
        return user;
    }

    /**
     * 기존 세션 정리
     */
    private void cleanupExistingSessions(int userId) {
        sessionMapper.deleteSessionsByUserId(userId);
        log.info("기존 세션 삭제 완료 - 사용자ID: {}", userId);
    }

    /**
     * 새 비밀번호 재설정 세션 생성
     */
    private String createPasswordResetSession(UserVO user) {
        String token = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(SESSION_EXPIRY_MINUTES);

        PasswordResetSessionVO session = getSession(token, user, expiresAt);

        sessionMapper.insertPasswordResetSession(session);

        log.debug("세션 저장 성공 - 사용자ID: {}, 만료시간: {}", user.getUserId(), expiresAt);
        return token;
    }

    /**
     * 비밀번호 재설정 세션 유효성 검사
     */
    private PasswordResetSessionVO validateResetSession(String token) {
        PasswordResetSessionVO session = sessionMapper.findValidSession(token, LocalDateTime.now());

        if (session == null) {
            log.warn("유효하지 않은 세션 - 토큰: {}", maskToken(token));
            throw new BaseException(BaseResponseStatus.INVALID_RESET_TOKEN);
        }

        log.info("세션 검증 성공 - 사용자ID: {}, 세션ID: {}", session.getUserId(), session.getId());
        return session;
    }

    /**
     * 사용자 비밀번호 업데이트
     */
    private void updateUserPassword(int userId, String newPassword, String token) {
        String encodedPassword = passwordEncoder.encode(newPassword);
       userMapper.updateUserPassword(userId, encodedPassword, LocalDateTime.now());

        log.info("비밀번호 업데이트 성공 - 사용자ID: {}", userId);
    }

    /**
     * 비밀번호 재설정 세션 완료 처리
     */
    private void completeResetSession(String token) {
        sessionMapper.markSessionAsCompleted(token); // 실패시 DB에서 예외 발생
        log.debug("세션 완료 처리 요청 - 토큰: {}", maskToken(token));
    }

    private PasswordResetSessionVO getSession(String token, UserVO user, LocalDateTime expiresAt) {
        // 현재: Setter 사용 (레거시 스타일)
        PasswordResetSessionVO session = new PasswordResetSessionVO();
        session.setToken(token);
        session.setUserId(user.getUserId());
        session.setExpiresAt(expiresAt);
        session.setCompleted(false);

        return session;
    }


    /**
     * 랜덤 토큰 생성
     */
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 토큰 마스킹 (로그용 - 보안)
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "***" + token.substring(token.length() - 4);
    }

    /**
     * OAuth 로그인은 차단
     */
    private void validateNotOAuthUser(UserVO user, String action) {
        if (user.getProvider() != null) {
            log.warn("OAuth 사용자의 {} 시도 차단 - provider: {}", action, user.getProvider());
            throw new BaseException(BaseResponseStatus.OAUTH_USER_ACCESS_DENIED);
        }
    }
}
