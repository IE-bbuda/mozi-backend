package org.iebbuda.mozi.domain.user.mapper;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.security.config.SecurityConfig;
import org.iebbuda.mozi.domain.user.domain.PasswordResetSessionVO;
import org.iebbuda.mozi.domain.user.domain.UserVO;
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
class PasswordResetSessionMapperTest {


    @Autowired
    private PasswordResetSessionMapper passwordMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private UserVO testUser;
    private PasswordResetSessionVO testSession;
    private final String TEST_TOKEN = "test-token-123456789";
    @BeforeEach
    void setUp() {
        log.info("테스트 시작 - 테스트용 데이터 준비");

        String randomNumber = UUID.randomUUID().toString().substring(0, 8);
        testUser = createTestUser(randomNumber);

        log.info("테스트 객체 생성 완료 - 사용자: {}, 토큰: {}", testUser.getLoginId(), TEST_TOKEN);
    }


    @Test
    @DisplayName("새 세션 생성 테스트")
    void insertPasswordResetSession_Success() {
        log.info("=== 새 세션 생성 테스트 시작 ===");

        // given - 사용자 생성 및 세션 객체 준비
        userMapper.insert(testUser);
        createSession(testUser.getUserId());

        // when - 세션 삽입
        int result = passwordMapper.insertPasswordResetSession(testSession);
        log.info("세션 생성 결과: {}, 생성된 ID: {}", result, testSession.getId());

        // then - 결과 검증
        assertEquals(1, result);
        assertNotNull(testSession); // useGeneratedKeys로 ID 자동 생성
    }

    @Test
    @DisplayName("사용자별 세션 삭제 테스트")
    void deleteSessionsByUserId_Success() {
        log.info("=== 사용자별 세션 삭제 테스트 시작 ===");

        // given
        userMapper.insert(testUser);
        createSession(testUser.getUserId());
        passwordMapper.insertPasswordResetSession(testSession);

        // when
        int deletedCount = passwordMapper.deleteSessionsByUserId(testUser.getUserId());

        // then
        assertEquals(1, deletedCount);

        // 삭제 확인
        PasswordResetSessionVO foundSession = passwordMapper.findValidSession(TEST_TOKEN, LocalDateTime.now());
        assertNull(foundSession);

        log.info("=== 사용자별 세션 삭제 테스트 완료 ===");
    }


    @Test
    @DisplayName("유효한 세션 조회 테스트")
    void findValidSession_Success() {
        log.info("=== 유효한 세션 조회 테스트 시작 ===");

        // given
        userMapper.insert(testUser);
        createSession(testUser.getUserId());
        passwordMapper.insertPasswordResetSession(testSession);

        // when
        PasswordResetSessionVO foundSession = passwordMapper.findValidSession(TEST_TOKEN, LocalDateTime.now());

        // then
        assertNotNull(foundSession);
        assertEquals(TEST_TOKEN, foundSession.getToken());
        assertEquals(testUser.getUserId(), foundSession.getUserId());
        assertFalse(foundSession.isCompleted());

        log.info("=== 유효한 세션 조회 테스트 완료 ===");
    }


    @Test
    @DisplayName("세션 완료 처리 테스트")
    void markSessionAsCompleted_Success() {
        // given - 세션 생성
        userMapper.insert(testUser);
        createSession(testUser.getUserId());
        passwordMapper.insertPasswordResetSession(testSession);

        // when - 완료 처리
        int result = passwordMapper.markSessionAsCompleted(TEST_TOKEN);

        // then - 결과 검증
        assertEquals(1, result);  // 1개 행 업데이트됨

        // 완료된 세션은 findValidSession에서 조회되지 않음
        PasswordResetSessionVO foundSession = passwordMapper.findValidSession(TEST_TOKEN, LocalDateTime.now());
        assertNull(foundSession);  // completed=true라서 조회 안됨
    }

    @Test
    @DisplayName("만료된 세션 삭제 테스트")
    void deleteExpiredSessions_Success() {
        log.info("=== 만료된 세션 삭제 테스트 시작 ===");

        // given - 만료된 세션 생성
        userMapper.insert(testUser);
        createExpiredSession(testUser.getUserId());
        passwordMapper.insertPasswordResetSession(testSession);

        // when
        int deletedCount = passwordMapper.deleteExpiredSessions(LocalDateTime.now());

        // then
        assertTrue(deletedCount >= 1);

        log.info("=== 만료된 세션 삭제 테스트 완료 ===");
    }


    private UserVO createTestUser(String randomNumber) {
        UserVO user = new UserVO();
        user.setUsername("테스트유저" + randomNumber);
        user.setLoginId("testuser" + randomNumber);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test" + randomNumber + "@email.com");
        user.setBirthDate("010203");
        return user;
    }

    private void createExpiredSession(int userId) {
        testSession = new PasswordResetSessionVO();
        testSession.setToken(TEST_TOKEN);
        testSession.setUserId(userId);
        testSession.setExpiresAt(LocalDateTime.now().minusMinutes(10)); // 10분 전 만료
        testSession.setCompleted(false);
    }

    private void createSession(int userId) {
        testSession = new PasswordResetSessionVO();
        testSession.setToken(TEST_TOKEN);
        testSession.setUserId(userId);
        testSession.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        testSession.setCompleted(false);

    }
}