package org.iebbuda.mozi.domain.user.service;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.security.config.SecurityConfig;
import org.iebbuda.mozi.domain.user.domain.PasswordResetSessionVO;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.iebbuda.mozi.domain.user.dto.request.PasswordResetRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.AccountVerificationRequestDTO;
import org.iebbuda.mozi.domain.user.mapper.PasswordResetSessionMapper;
import org.iebbuda.mozi.domain.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})
@Log4j2
@Transactional
@Rollback
class PasswordResetServiceTest {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordResetSessionMapper sessionMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationService emailVerificationService; // 추가

    private UserVO testUser;
    private String randomNumber;

    @BeforeEach
    void setUp() {
        randomNumber = UUID.randomUUID().toString().substring(0, 8);
        testUser = createTestUser(randomNumber);
        userMapper.insert(testUser);

        log.info("테스트 사용자 생성 완료 - 로그인ID: {}, 이메일: {}", testUser.getLoginId(), testUser.getEmail());
    }

    @Test
    @DisplayName("계정 확인 및 세션 생성 - 성공")
    void verifyAccountAndIssueToken_ValidUser_Success() {
        log.info("=== 계정 확인 및 세션 생성 테스트 시작 ===");

        // given
        // 테스트용 이메일 인증 상태 설정
        emailVerificationService.setEmailVerifiedForTest(testUser.getEmail());

        AccountVerificationRequestDTO request = AccountVerificationRequestDTO.builder()
                .loginId(testUser.getLoginId())
                .email(testUser.getEmail())
                .build();

        // when
        String token = passwordResetService.verifyAccountAndIssueToken(request);

        // then
        assertNotNull(token);
        assertEquals(32, token.length()); // UUID에서 '-' 제거한 길이
        log.info("토큰 ={}", token);
        log.info("=== 계정 확인 및 세션 생성 테스트 완료 ===");
    }

    @Test
    @DisplayName("계정 확인 - 이메일 인증 미완료")
    void verifyAccountAndIssueToken_EmailNotVerified_ThrowsException() {
        log.info("=== 이메일 인증 미완료 계정 확인 테스트 시작 ===");

        // given - 이메일 인증 상태 설정하지 않음
        AccountVerificationRequestDTO request = AccountVerificationRequestDTO.builder()
                .loginId(testUser.getLoginId())
                .email(testUser.getEmail())
                .build();

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            passwordResetService.verifyAccountAndIssueToken(request);
        });

        assertEquals(BaseResponseStatus.EMAIL_NOT_VERIFIED, exception.getStatus());
        log.info("예외 상태 = {}", exception.getStatus());
        log.info("예외 메시지 = {}", exception.getMessage());
        log.info("=== 이메일 인증 미완료 계정 확인 테스트 완료 ===");
    }

    @Test
    @DisplayName("계정 확인 - 존재하지 않는 사용자")
    void verifyAccountAndIssueToken_UserNotFound_ThrowsException() {
        log.info("=== 존재하지 않는 사용자 계정 확인 테스트 시작 ===");

        // given
        // 테스트용 이메일 인증 상태 설정 (존재하지 않는 이메일이지만 테스트용)
        emailVerificationService.setEmailVerifiedForTest("nonexistent@email.com");

        AccountVerificationRequestDTO request = AccountVerificationRequestDTO.builder()
                .loginId("nonexistent")
                .email("nonexistent@email.com")
                .build();

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            passwordResetService.verifyAccountAndIssueToken(request);
        });

        assertEquals(BaseResponseStatus.USER_NOT_FOUND_FOR_RESET, exception.getStatus());
        log.info("예외 상태 = {}", exception.getStatus());
        log.info("예외 메시지 = {}", exception.getMessage());
        log.info("=== 존재하지 않는 사용자 계정 확인 테스트 완료 ===");
    }

    @Test
    @DisplayName("계정 확인 - 이메일 불일치")
    void verifyAccountAndIssueToken_EmailMismatch_ThrowsException() {
        log.info("=== 이메일 불일치 계정 확인 테스트 시작 ===");

        // given
        // 테스트용 이메일 인증 상태 설정 (잘못된 이메일이지만 테스트용)
        emailVerificationService.setEmailVerifiedForTest("wrong@email.com");

        AccountVerificationRequestDTO request = AccountVerificationRequestDTO.builder()
                .loginId(testUser.getLoginId())
                .email("wrong@email.com")
                .build();

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            passwordResetService.verifyAccountAndIssueToken(request);
        });

        assertEquals(BaseResponseStatus.USER_NOT_FOUND_FOR_RESET, exception.getStatus());
        log.info("예외 상태 = {}", exception.getStatus());
        log.info("예외 메시지 = {}", exception.getMessage());
        log.info("=== 이메일 불일치 계정 확인 테스트 완료 ===");
    }

    @Test
    @DisplayName("기존 세션 정리 기능 확인")
    void verifyAccountAndIssueToken_CleanupExistingSessions() {
        log.info("=== 기존 세션 정리 기능 테스트 시작 ===");

        // given
        // 테스트용 이메일 인증 상태 설정
        emailVerificationService.setEmailVerifiedForTest(testUser.getEmail());

        AccountVerificationRequestDTO request = AccountVerificationRequestDTO.builder()
                .loginId(testUser.getLoginId())
                .email(testUser.getEmail())
                .build();

        String firstToken = passwordResetService.verifyAccountAndIssueToken(request);
        assertNotNull(sessionMapper.findValidSession(firstToken, LocalDateTime.now()));
        log.info("첫번째 토큰: {}", firstToken);

        // when - 새로운 세션 생성 (기존 세션 정리됨)
        String secondToken = passwordResetService.verifyAccountAndIssueToken(request);
        log.info("두번째 토큰: {}", secondToken);

        // then
        assertNotEquals(firstToken, secondToken);

        // 기존 세션은 삭제되어야 함
        assertNull(sessionMapper.findValidSession(firstToken, LocalDateTime.now()));

        // 새 세션은 존재해야 함
        assertNotNull(sessionMapper.findValidSession(secondToken, LocalDateTime.now()));
        log.info("=== 기존 세션 정리 기능 테스트 완료 ===");
    }

    @Test
    @DisplayName("비밀번호 재설정 - 성공")
    void resetPassword_ValidToken_Success() {
        log.info("=== 비밀번호 재설정 테스트 시작 ===");

        // given - 세션 생성
        // 테스트용 이메일 인증 상태 설정
        emailVerificationService.setEmailVerifiedForTest(testUser.getEmail());

        AccountVerificationRequestDTO verifyRequest = AccountVerificationRequestDTO.builder()
                .loginId(testUser.getLoginId())
                .email(testUser.getEmail())
                .build();

        String token = passwordResetService.verifyAccountAndIssueToken(verifyRequest);

        // 비밀번호 재설정 요청
        PasswordResetRequestDTO resetRequest = PasswordResetRequestDTO.builder()
                .token(token)
                .newPassword("newPassword123")
                .build();

        String oldPassword = testUser.getPassword();

        // when
        assertDoesNotThrow(() -> {
            passwordResetService.resetPassword(resetRequest);
        });

        // then
        // 세션이 완료 처리되었는지 확인
        PasswordResetSessionVO session = sessionMapper.findValidSession(token, LocalDateTime.now());
        assertNull(session); // completed=true이므로 조회되지 않음

        // 비밀번호가 변경되었는지 확인 (실제 DB에서 다시 조회)
        UserVO updatedUser = userMapper.findByUserId(testUser.getUserId());
        assertNotEquals(oldPassword, updatedUser.getPassword());
        assertTrue(passwordEncoder.matches("newPassword123", updatedUser.getPassword()));

        log.info("=== 비밀번호 재설정 테스트 완료 ===");
    }

    @Test
    @DisplayName("비밀번호 재설정 - 유효하지 않은 토큰")
    void resetPassword_InvalidToken_ThrowsException() {
        log.info("=== 유효하지 않은 토큰 비밀번호 재설정 테스트 시작 ===");

        // given
        PasswordResetRequestDTO resetRequest = PasswordResetRequestDTO.builder()
                .token("invalid-token")
                .newPassword("newPassword123")
                .build();

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            passwordResetService.resetPassword(resetRequest);
        });

        assertEquals(BaseResponseStatus.INVALID_RESET_TOKEN, exception.getStatus());
        log.info("예외 상태: {}", exception.getStatus());
        log.info("=== 유효하지 않은 토큰 비밀번호 재설정 테스트 완료 ===");
    }

    @Test
    @DisplayName("비밀번호 재설정 - 만료된 토큰")
    void resetPassword_ExpiredToken_ThrowsException() {
        log.info("=== 만료된 토큰 비밀번호 재설정 테스트 시작 ===");

        // given - 만료된 세션을 직접 생성
        PasswordResetSessionVO expiredSession = new PasswordResetSessionVO();
        expiredSession.setToken("expired-token");
        expiredSession.setUserId(testUser.getUserId());
        expiredSession.setExpiresAt(LocalDateTime.now().minusMinutes(10)); // 10분 전 만료
        expiredSession.setCompleted(false);
        sessionMapper.insertPasswordResetSession(expiredSession);

        PasswordResetRequestDTO resetRequest = PasswordResetRequestDTO.builder()
                .token("expired-token")
                .newPassword("newPassword123")
                .build();

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            passwordResetService.resetPassword(resetRequest);
        });

        assertEquals(BaseResponseStatus.INVALID_RESET_TOKEN, exception.getStatus());
        log.info("예외 상태: {}", exception.getStatus());
        log.info("=== 만료된 토큰 비밀번호 재설정 테스트 완료 ===");
    }

    @Test
    @DisplayName("비밀번호 재설정 - 이미 완료된 토큰")
    void resetPassword_CompletedToken_ThrowsException() {
        log.info("=== 이미 완료된 토큰 비밀번호 재설정 테스트 시작 ===");

        // given - 세션 생성 후 완료 처리
        // 테스트용 이메일 인증 상태 설정
        emailVerificationService.setEmailVerifiedForTest(testUser.getEmail());

        AccountVerificationRequestDTO verifyRequest = AccountVerificationRequestDTO.builder()
                .loginId(testUser.getLoginId())
                .email(testUser.getEmail())
                .build();

        String token = passwordResetService.verifyAccountAndIssueToken(verifyRequest);

        // 세션을 완료 처리
        sessionMapper.markSessionAsCompleted(token);

        PasswordResetRequestDTO resetRequest = PasswordResetRequestDTO.builder()
                .token(token)
                .newPassword("newPassword123")
                .build();

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            passwordResetService.resetPassword(resetRequest);
        });

        assertEquals(BaseResponseStatus.INVALID_RESET_TOKEN, exception.getStatus());
        log.info("=== 이미 완료된 토큰 비밀번호 재설정 테스트 완료 ===");
    }

    private UserVO createTestUser(String randomNumber) {
        LocalDateTime now = LocalDateTime.now();

        UserVO user = new UserVO();
        user.setUsername("테스트유저" + randomNumber);
        user.setLoginId("testuser" + randomNumber);
        user.setPassword(passwordEncoder.encode("originalPassword123"));
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test" + randomNumber + "@email.com");
        user.setBirthDate("010203");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;
    }
}