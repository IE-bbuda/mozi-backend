package org.iebbuda.mozi.domain.profile.service;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.domain.enums.*;
import org.iebbuda.mozi.domain.profile.dto.UserProfileInfoDTO;
import org.iebbuda.mozi.domain.profile.mapper.UserProfileMapper;
import org.iebbuda.mozi.domain.security.config.SecurityConfig;
import org.iebbuda.mozi.domain.user.domain.UserVO;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})
@Log4j2
@Transactional
@Rollback
class UserProfileServiceImplTest {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserVO testUser;
    private UserProfileInfoDTO testProfileDTO;
    private String randomNumber;

    @BeforeEach
    void setUp() {
        log.info("테스트 시작 - 테스트용 데이터 준비");

        randomNumber = UUID.randomUUID().toString().substring(0, 8);
        testUser = createTestUser(randomNumber);
        userMapper.insert(testUser);

        testProfileDTO = createTestProfileDTO();

        log.info("테스트 객체 생성 완료 - 사용자: {}", testUser.getLoginId());
    }


    @Test
    @DisplayName("프로필 신규 저장 - 성공")
    void saveProfile_NewProfile_Success() {
        log.info("=== 프로필 신규 저장 테스트 시작 ===");

        // when - 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> {
            userProfileService.saveProfile(testUser.getLoginId(), testProfileDTO);
        });

        // then - DB에 실제로 저장되었는지 확인
        UserProfileVO savedProfile = userProfileMapper.findByUserId(testUser.getUserId());
        assertNotNull(savedProfile);
        assertEquals(Region.SEOUL, savedProfile.getRegion());
        assertEquals(25, savedProfile.getAge());
        assertEquals(MaritalStatus.SINGLE, savedProfile.getMaritalStatus());

        log.info("프로필 신규 저장 검증 완료");
        log.info("=== 프로필 신규 저장 테스트 완료 ===");
    }


    @Test
    @DisplayName("프로필 수정 - 성공")
    void saveProfile_UpdateProfile_Success() {
        log.info("=== 프로필 수정 테스트 시작 ===");

        // given - 기존 프로필 저장
        userProfileService.saveProfile(testUser.getLoginId(), testProfileDTO);

        // 수정할 프로필 정보
        UserProfileInfoDTO updateDTO = createUpdateProfileDTO();

        // when - 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> {
            userProfileService.saveProfile(testUser.getLoginId(), updateDTO);
        });

        // then - DB에 실제로 수정되었는지 확인
        UserProfileVO updatedProfile = userProfileMapper.findByUserId(testUser.getUserId());
        assertNotNull(updatedProfile);
        assertEquals(Region.BUSAN, updatedProfile.getRegion());
        assertEquals(30, updatedProfile.getAge());
        assertEquals(MaritalStatus.MARRIED, updatedProfile.getMaritalStatus());

        log.info("프로필 수정 검증 완료");
        log.info("=== 프로필 수정 테스트 완료 ===");
    }

    @Test
    @DisplayName("프로필 조회 - 존재하지 않는 경우")
    void getUserProfile_NotExists() {
        log.info("=== 프로필 조회 테스트 시작 (존재하지 않음) ===");

        // when
        UserProfileInfoDTO result = userProfileService.getUserProfile(testUser.getLoginId());

        // then - 빈 객체가 반환되어야 함
        assertNotNull(result);
        assertNull(result.getRegion());
        assertNull(result.getAge());
        assertNull(result.getMaritalStatus());
        assertNull(result.getAnnualIncome());

        log.info("빈 프로필 조회 검증 완료");
        log.info("=== 프로필 조회 테스트 완료 ===");
    }

    @Test
    @DisplayName("프로필 조회 - 존재하는 경우")
    void getUserProfile_Exists() {
        log.info("=== 프로필 조회 테스트 시작 (존재함) ===");

        // given - 프로필 저장
        userProfileService.saveProfile(testUser.getLoginId(), testProfileDTO);

        // when
        UserProfileInfoDTO result = userProfileService.getUserProfile(testUser.getLoginId());

        // then
        assertNotNull(result);
        assertEquals(Region.SEOUL, result.getRegion());
        assertEquals(25, result.getAge());
        assertEquals(MaritalStatus.SINGLE, result.getMaritalStatus());
        assertEquals(0, new BigDecimal("30000000.00").compareTo(result.getAnnualIncome()));
        assertEquals(EducationLevel.GRADUATE, result.getEducationLevel());
        assertEquals(EmploymentStatus.EMPLOYED, result.getEmploymentStatus());
        assertEquals(Major.BUSINESS, result.getMajor());
        assertEquals(Specialty.NO_RESTRICTION, result.getSpecialty());

        log.info("기존 프로필 조회 검증 완료");
        log.info("=== 프로필 조회 테스트 완료 ===");
    }

    @Test
    @DisplayName("부분적으로 채워진 프로필 저장 및 조회")
    void saveProfile_PartialData_Success() {
        log.info("=== 부분적으로 채워진 프로필 테스트 시작 ===");

        // given - 일부만 채워진 프로필
        UserProfileInfoDTO partialProfile = UserProfileInfoDTO.builder()
                .region(Region.SEOUL)
                .age(25)
                .build(); // 나머지 필드는 null

        // when - 저장
        assertDoesNotThrow(() -> {
            userProfileService.saveProfile(testUser.getLoginId(), partialProfile);
        });

        // then - 조회 및 검증
        UserProfileInfoDTO result = userProfileService.getUserProfile(testUser.getLoginId());
        assertNotNull(result);
        assertEquals(Region.SEOUL, result.getRegion());
        assertEquals(25, result.getAge());
        assertNull(result.getMaritalStatus()); // null이어야 함
        assertNull(result.getAnnualIncome()); // null이어야 함

        log.info("부분 프로필 저장/조회 검증 완료");
        log.info("=== 부분적으로 채워진 프로필 테스트 완료 ===");
    }


    private UserVO createTestUser(String randomNumber) {
        LocalDateTime now = LocalDateTime.now();

        UserVO user = new UserVO();
        user.setUsername("테스트유저" + randomNumber);
        user.setLoginId("testuser" + randomNumber);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test" + randomNumber + "@email.com");
        user.setBirthDate("010203");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 프로필 저장 - BaseException 발생")
    void saveProfile_UserNotFound_ThrowsBaseException() {
        log.info("=== 존재하지 않는 사용자 프로필 저장 테스트 시작 ===");

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userProfileService.saveProfile("nonexistent", testProfileDTO);
        });

        assertEquals(BaseResponseStatus.INVALID_MEMBER, exception.getStatus());
        log.info("예외 상태: {}", exception.getStatus());
        log.info("=== 존재하지 않는 사용자 프로필 저장 테스트 완료 ===");
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 프로필 조회 - BaseException 발생")
    void getUserProfile_UserNotFound_ThrowsBaseException() {
        log.info("=== 존재하지 않는 사용자 프로필 조회 테스트 시작 ===");

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userProfileService.getUserProfile("nonexistent");
        });

        assertEquals(BaseResponseStatus.INVALID_MEMBER, exception.getStatus());
        log.info("예외 상태: {}", exception.getStatus());
        log.info("=== 존재하지 않는 사용자 프로필 조회 테스트 완료 ===");
    }


    private UserProfileInfoDTO createTestProfileDTO() {
        return UserProfileInfoDTO.builder()
                .region(Region.SEOUL)
                .age(25)
                .maritalStatus(MaritalStatus.SINGLE)
                .annualIncome(new BigDecimal("30000000.00"))
                .educationLevel(EducationLevel.GRADUATE)
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .major(Major.BUSINESS)
                .specialty(Specialty.NO_RESTRICTION)
                .build();
    }

    private UserProfileInfoDTO createUpdateProfileDTO() {
        return UserProfileInfoDTO.builder()
                .region(Region.BUSAN)
                .age(30)
                .maritalStatus(MaritalStatus.MARRIED)
                .annualIncome(new BigDecimal("50000000.00"))
                .educationLevel(EducationLevel.GRADUATE)
                .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                .major(Major.ENGINEERING)
                .specialty(Specialty.WOMEN)
                .build();
    }
}