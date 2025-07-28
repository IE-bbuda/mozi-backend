package org.iebbuda.mozi.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.user.domain.PasswordResetSessionVO;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.iebbuda.mozi.domain.user.dto.request.PasswordResetRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.PasswordResetVerifyRequestDTO;
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

    // 세션 유효 시간 (분)
    private static final int SESSION_EXPIRY_MINUTES = 30;


    /**
     * 1단계: 계정 확인 및 세션 생성
     */
    @Transactional
    public String verifyAccount(PasswordResetVerifyRequestDTO request) {
        log.info("비밀번호 재설정 계정 확인 시작 - 로그인ID: {}", request.getLoginId());

        // 1. 사용자 조회 및 검증
        UserVO user = findAndValidateUser(request.getLoginId(), request.getEmail());

        // 2. 기존 세션 정리
        cleanupExistingSessions(user.getUserId());

        // 3. 새 세션 생성 및 저장
        String token = createPasswordResetSession(user);

        log.info("비밀번호 재설정 세션 생성 완료 - 사용자ID: {}, 토큰: {}",
                user.getUserId(), maskToken(token));

        return token;
    }

    /**
     * 2단계: 비밀번호 재설정
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
}
