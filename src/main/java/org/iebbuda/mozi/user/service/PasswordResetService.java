package org.iebbuda.mozi.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.user.domain.PasswordResetSessionVO;
import org.iebbuda.mozi.user.domain.UserVO;
import org.iebbuda.mozi.user.dto.request.PasswordResetRequestDTO;
import org.iebbuda.mozi.user.dto.request.PasswordResetVerifyRequestDTO;
import org.iebbuda.mozi.user.dto.response.PasswordResetResponseDTO;
import org.iebbuda.mozi.user.dto.response.PasswordResetVerifyResponseDTO;
import org.iebbuda.mozi.user.dto.response.SessionStatsResponseDTO;
import org.iebbuda.mozi.user.mapper.PasswordResetSessionMapper;
import org.iebbuda.mozi.user.mapper.UserMapper;

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
    public PasswordResetVerifyResponseDTO verifyAccount(PasswordResetVerifyRequestDTO request) {
        log.info("비밀번호 재설정 계정 확인 시작 - 로그인ID: {}", request.getLoginId());

        try {
            // 1. 사용자 조회 (기존 UserMapper 활용)
            UserVO user = userMapper.findByLoginIdAndEmail(request.getLoginId(), request.getEmail());

            if (user == null) {
                log.warn("일치하는 계정 없음 - 로그인ID: {}, 이메일: {}", request.getLoginId(), request.getEmail());
                return PasswordResetVerifyResponseDTO.failed();
            }

            // 3. 기존 세션 정리 (동일 사용자의 이전 요청들 삭제)
            int deletedSessions = sessionMapper.deleteSessionsByUserId(user.getUserId());
            log.info("기존 세션 삭제 완료 - 사용자ID: {}, 삭제된 세션 수: {}", user.getUserId(), deletedSessions);

            // 4. 새 세션 생성
            String token = generateToken();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(SESSION_EXPIRY_MINUTES);

            PasswordResetSessionVO session = getSession(token, user, expiresAt);

            // 5. 세션 저장 및 결과 검증
            int insertResult = sessionMapper.insertPasswordResetSession(session);
            if (insertResult != 1) {
                log.error("세션 생성 실패 - 사용자ID: {}, 영향받은 행: {}", user.getUserId(), insertResult);
                return PasswordResetVerifyResponseDTO.failed("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }

            log.info("비밀번호 재설정 세션 생성 완료 - 사용자ID: {}, 토큰: {}, 만료시간: {}",
                    user.getUserId(), maskToken(token), expiresAt);

            return PasswordResetVerifyResponseDTO.success(token);

        } catch (Exception e) {
            log.error("계정 확인 중 오류 발생 - 로그인ID: {}", request.getLoginId(), e);
            return PasswordResetVerifyResponseDTO.failed("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    /**
     * 2단계: 비밀번호 재설정
     */
    @Transactional
    public PasswordResetResponseDTO resetPassword(PasswordResetRequestDTO request) {
        log.info("비밀번호 재설정 시작 - 토큰: {}", maskToken(request.getToken()));

        try {
            // 1. 세션 유효성 검사
            PasswordResetSessionVO session = sessionMapper.findValidSession(
                    request.getToken(),
                    LocalDateTime.now()
            );

            if (session == null) {
                log.warn("유효하지 않은 세션 - 토큰: {}", maskToken(request.getToken()));
                return PasswordResetResponseDTO.failed("유효하지 않거나 만료된 요청입니다.");
            }

            log.info("세션 검증 성공 - 사용자ID: {}, 세션ID: {}", session.getUserId(), session.getId());

            // 2. 비밀번호 업데이트 (기존 UserMapper 활용)
            int updateResult = userMapper.updateUserPassword(
                    session.getUserId(),
                    passwordEncoder.encode(request.getNewPassword()), //암호화된 비밀번호
                    LocalDateTime.now()
            );

            if (updateResult != 1) {
                log.error("비밀번호 업데이트 실패 - 사용자ID: {}, 토큰: {}, 영향받은 행: {}",
                        session.getUserId(), maskToken(request.getToken()), updateResult);
                return PasswordResetResponseDTO.failed("비밀번호 변경 중 오류가 발생했습니다.");
            }

            log.info("비밀번호 업데이트 성공 - 사용자ID: {}", session.getUserId());

            // 3. 세션 완료 처리
            int completeResult = sessionMapper.markSessionAsCompleted(request.getToken());
            if (completeResult != 1) {
                log.warn("세션 완료 처리 실패 - 토큰: {}, 영향받은 행: {}",
                        maskToken(request.getToken()), completeResult);
                // 비밀번호는 이미 변경됐으므로 경고만 로그
            }

            log.info("비밀번호 재설정 완료 - 사용자ID: {}, 세션ID: {}", session.getUserId(), session.getId());

            return PasswordResetResponseDTO.success();

        } catch (Exception e) {
            log.error("비밀번호 재설정 중 오류 발생 - 토큰: {}", maskToken(request.getToken()), e);
            return PasswordResetResponseDTO.failed("비밀번호 재설정 중 오류가 발생했습니다.");
        }
    }

    private PasswordResetSessionVO getSession(String token, UserVO user, LocalDateTime expiresAt) {
        // 현재: Setter 사용 (레거시 스타일)
        PasswordResetSessionVO session = new PasswordResetSessionVO();
        session.setToken(token);
        session.setUserId(user.getUserId());
        session.setExpiresAt(expiresAt);
        session.setCompleted(false);
        session.setCreatedAt(LocalDateTime.now());
        return session;
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            PasswordResetSessionVO session = sessionMapper.findValidSession(
                    token,
                    LocalDateTime.now()
            );

            boolean isValid = session != null;
            log.info("토큰 유효성 검사 결과 - 토큰: {}, 유효성: {}", maskToken(token), isValid);

            return isValid;

        } catch (Exception e) {
            log.error("토큰 유효성 검사 중 오류 발생 - 토큰: {}", maskToken(token), e);
            return false;
        }
    }

    /**
     * 만료된 세션 정리 (스케줄러에서 호출)
     */
    @Transactional
    public void cleanupExpiredSessions() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int deletedCount = sessionMapper.deleteExpiredSessions(now);

            if (deletedCount > 0) {
                log.info("만료된 세션 정리 완료 - 삭제된 세션 수: {}, 기준시간: {}", deletedCount, now);
            } else {
                log.debug("만료된 세션 없음 - 기준시간: {}", now);
            }

        } catch (Exception e) {
            log.error("만료된 세션 정리 중 오류 발생", e);
        }
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
