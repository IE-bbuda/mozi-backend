package org.iebbuda.mozi.domain.user.service;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.security.config.SecurityConfig;



import org.iebbuda.mozi.domain.user.dto.response.LoginIdFindResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.UserDTO;
import org.iebbuda.mozi.domain.user.dto.request.UserJoinRequestDTO;


import org.iebbuda.mozi.domain.user.service.UserService;
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


import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})
@Log4j2
@Transactional
@Rollback
class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationService emailVerificationService; // 추가

    private UserJoinRequestDTO joinRequest;
    private String randomNumber;

    @BeforeEach
    void setUp() {
        randomNumber = UUID.randomUUID().toString().substring(0, 8);
        joinRequest = createTestJoinRequest();
    }

    @Test
    @DisplayName("로그인 ID 중복 체크 - 중복되지 않은 경우")
    void checkDuplicate_WhenNoExists() {
        String loginId = "newUser"+randomNumber;

        boolean result = userService.checkDuplicate(loginId);
        assertTrue(result);
        log.info("중복체크 결과 (새 아이디): {}", loginId);
    }

    @Test
    @DisplayName("로그인 ID 중복 체크 - 중복된 경우")
    void checkDuplicate_WhenExists() {
        // 테스트용 이메일 인증 상태 설정
        emailVerificationService.setEmailVerifiedForTest(joinRequest.getEmail());

        userService.join(joinRequest);

        String existingLoginId = joinRequest.getLoginId();

        boolean result = userService.checkDuplicate(existingLoginId);
        assertFalse(result);
        log.info("중복체크 결과 (기존 아이디): {}", existingLoginId);
    }

    @Test
    @DisplayName("회원가입")
    void join() {
        // 테스트용 이메일 인증 상태 설정
        emailVerificationService.setEmailVerifiedForTest(joinRequest.getEmail());

        int userId = userService.join(joinRequest);

        assertNotNull(userService.get(userId));
        assertTrue(userId > 0);
        log.info("joinResult={}", userService.get(userId));
    }

    @Test
    @DisplayName("userId로 사용자 조회")
    void get(){
        // 테스트용 이메일 인증 상태 설정
        emailVerificationService.setEmailVerifiedForTest(joinRequest.getEmail());

        int userId= userService.join(joinRequest);

        // when
        Optional<UserDTO> result = userService.get(userId);

        assertTrue(result.isPresent());

        assertNotNull(result.map(UserDTO::getUpdatedAt));
        assertNotNull(result.map(UserDTO::getCreatedAt));
        log.info("result={}",result.get());
    }

    @Test
    @DisplayName("이메일로 로그인ID 찾기")
    void findLoginIdByEmail(){
        // 테스트용 이메일 인증 상태 설정
        emailVerificationService.setEmailVerifiedForTest(joinRequest.getEmail());

        int userId = userService.join(joinRequest);
        log.info("userId: {}", userId);

        Optional<UserDTO> user = userService.get(userId);

        // Optional이 존재하는지 확인
        assertTrue(user.isPresent(), "사용자가 존재해야 합니다");

        UserDTO userDto = user.get();
        LoginIdFindResponseDTO result = userService
                .findLoginIdByEmail(userDto.getUsername(), userDto.getEmail());

        assertNotNull(result);
        assertTrue(result.isFound());
        log.info("result={}", result.getMaskedLoginId());
    }

    @Test
    @DisplayName("이메일로 로그인ID 찾기 실패 - 존재하지 않는 사용자")
    void findLoginIdByEmailNotFound() {
        // Given: 존재하지 않는 사용자 정보
        String nonExistentUsername = "존재하지않는사용자";
        String nonExistentEmail = "notfound@email.com";

        // When: 존재하지 않는 정보로 로그인ID 찾기
        LoginIdFindResponseDTO result = userService.findLoginIdByEmail(
                nonExistentUsername,
                nonExistentEmail
        );

        // Then: 찾지 못함 결과 검증
        assertNotNull(result);
        assertFalse(result.isFound());
        assertNull(result.getMaskedLoginId());

        log.info("로그인ID 찾기 실패 (정상): {}", result.getMaskedLoginId());
    }

    private UserJoinRequestDTO createTestJoinRequest() {
        return UserJoinRequestDTO.builder()
                .loginId("testuser" + randomNumber)
                .username("테스트유저" + randomNumber)
                .password(passwordEncoder.encode("password123"))
                .email("test" + randomNumber + "@email.com")
                .phoneNumber("010-1234-5678")
                .birthDate("010607")
                .build();
    }
}