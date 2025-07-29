package org.iebbuda.mozi.domain.profile.mapper;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.domain.enums.*;
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
class UserProfileMapperTest {
    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserVO testUser;
    private UserProfileVO testProfile;
    private String randomNumber;

    @BeforeEach
    void setUp() {
        log.info("테스트 시작 - 테스트용 데이터 준비");

        randomNumber = UUID.randomUUID().toString().substring(0, 8);
        testUser = createTestUser(randomNumber);
        userMapper.insert(testUser);

        testProfile = createTestProfile(testUser.getUserId());

        log.info("테스트 객체 생성 완료 - 사용자ID: {}", testUser.getUserId());
    }


    @Test
    @DisplayName("개인정보 존재 여부 확인 - 존재하지 않는 경우")
    void hasPersonalInfo_NotExists() {
        log.info("=== 개인정보 존재 여부 확인 테스트 시작 (존재하지 않음) ===");

        // when
        Boolean hasInfo = userProfileMapper.hasPersonalInfo(testUser.getUserId());

        // then
        assertFalse(hasInfo);
        log.info("개인정보 존재 여부: {}", hasInfo);

        log.info("=== 개인정보 존재 여부 확인 테스트 완료 ===");
    }

    @Test
    @DisplayName("사용자 프로필 조회 - 존재하지 않는 경우")
    void findByUserId_NotExists() {
        log.info("=== 사용자 프로필 조회 테스트 시작 (존재하지 않음) ===");

        // when
        UserProfileVO foundProfile = userProfileMapper.findByUserId(testUser.getUserId());

        // then
        assertNull(foundProfile);
        log.info("조회된 프로필: {}", foundProfile);

        log.info("=== 사용자 프로필 조회 테스트 완료 ===");
    }

    @Test
    @DisplayName("사용자 프로필 신규 생성")
    void insertUserProfile_Success() {
        log.info("=== 사용자 프로필 신규 생성 테스트 시작 ===");

        // when
        int result = userProfileMapper.insertUserProfile(testProfile);

        // then
        assertEquals(1, result);

        // 생성된 프로필 조회로 확인
        UserProfileVO foundProfile = userProfileMapper.findByUserId(testUser.getUserId());
        assertNotNull(foundProfile);
        assertEquals(testProfile.getRegion(), foundProfile.getRegion());
        assertEquals(testProfile.getAge(), foundProfile.getAge());
        assertEquals(testProfile.getMaritalStatus(), foundProfile.getMaritalStatus());
        assertEquals(testProfile.getAnnualIncome(), foundProfile.getAnnualIncome());
        assertEquals(testProfile.getEducationLevel(), foundProfile.getEducationLevel());
        assertEquals(testProfile.getEmploymentStatus(), foundProfile.getEmploymentStatus());
        assertEquals(testProfile.getMajor(), foundProfile.getMajor());
        assertEquals(testProfile.getSpecialty(), foundProfile.getSpecialty());

        log.info("프로필 생성 결과: {}, 생성된 프로필: {}", result, foundProfile);

        log.info("=== 사용자 프로필 신규 생성 테스트 완료 ===");
    }

    @Test
    @DisplayName("사용자 프로필 조회 - 존재하는 경우")
    void findByUserId_Exists() {
        log.info("=== 사용자 프로필 조회 테스트 시작 (존재함) ===");

        // given - 프로필 생성
        userProfileMapper.insertUserProfile(testProfile);

        // when
        UserProfileVO foundProfile = userProfileMapper.findByUserId(testUser.getUserId());

        // then
        assertNotNull(foundProfile);
        assertEquals(testUser.getUserId(), foundProfile.getUserId());
        assertEquals(testProfile.getRegion(), foundProfile.getRegion());
        assertEquals(testProfile.getAge(), foundProfile.getAge());

        log.info("조회된 프로필: {}", foundProfile);

        log.info("=== 사용자 프로필 조회 테스트 완료 ===");
    }

    @Test
    @DisplayName("사용자 프로필 업데이트")
    void updateUserProfile_Success() {
        log.info("=== 사용자 프로필 업데이트 테스트 시작 ===");

        // given - 프로필 생성
        userProfileMapper.insertUserProfile(testProfile);

        // 업데이트할 데이터 준비
        testProfile.setRegion(Region.BUSAN);
        testProfile.setAge(30);
        testProfile.setMaritalStatus(MaritalStatus.MARRIED);
        testProfile.setAnnualIncome(new BigDecimal("50000000.00")); // 5천만원
        testProfile.setEducationLevel(EducationLevel.GRADUATE);
        testProfile.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
        testProfile.setMajor(Major.ENGINEERING);

        // when
        int result = userProfileMapper.updateUserProfile(testProfile);

        // then
        assertEquals(1, result);

        // 업데이트된 프로필 조회로 확인
        UserProfileVO updatedProfile = userProfileMapper.findByUserId(testUser.getUserId());
        assertNotNull(updatedProfile);
        assertEquals(Region.BUSAN, updatedProfile.getRegion());
        assertEquals(30, updatedProfile.getAge());
        assertEquals(MaritalStatus.MARRIED, updatedProfile.getMaritalStatus());
        assertEquals(0, new BigDecimal("50000000.00").compareTo(updatedProfile.getAnnualIncome())); // BigDecimal 비교
        assertEquals(EducationLevel.GRADUATE, updatedProfile.getEducationLevel());
        assertEquals(EmploymentStatus.SELF_EMPLOYED, updatedProfile.getEmploymentStatus());
        assertEquals(Major.ENGINEERING, updatedProfile.getMajor());

        log.info("프로필 업데이트 결과: {}, 업데이트된 프로필: {}", result, updatedProfile);

        log.info("=== 사용자 프로필 업데이트 테스트 완료 ===");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 프로필 업데이트")
    void updateUserProfile_NotExists() {
        log.info("=== 존재하지 않는 프로필 업데이트 테스트 시작 ===");

        // when - 존재하지 않는 프로필 업데이트 시도
        int result = userProfileMapper.updateUserProfile(testProfile);

        // then - 업데이트된 행이 없어야 함
        assertEquals(0, result);

        log.info("존재하지 않는 프로필 업데이트 결과: {}", result);

        log.info("=== 존재하지 않는 프로필 업데이트 테스트 완료 ===");
    }


    @Test
    @DisplayName("프로필 생성 후 개인정보 존재 여부 확인")
    void hasPersonalInfo_AfterInsert() {
        log.info("=== 프로필 생성 후 개인정보 존재 여부 확인 테스트 시작 ===");

        // given - 프로필 생성
        userProfileMapper.insertUserProfile(testProfile);

        // when
        Boolean hasInfo = userProfileMapper.hasPersonalInfo(testUser.getUserId());

        // then
        assertTrue(hasInfo);
        log.info("프로필 생성 후 개인정보 존재 여부: {}", hasInfo);

        log.info("=== 프로필 생성 후 개인정보 존재 여부 확인 테스트 완료 ===");
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

    private UserProfileVO createTestProfile(int userId) {
        UserProfileVO profile = new UserProfileVO();
        profile.setUserId(userId);
        profile.setRegion(Region.SEOUL);
        profile.setAge(25);
        profile.setMaritalStatus(MaritalStatus.SINGLE);
        profile.setAnnualIncome(new BigDecimal("30000000.00")); // 3천만원
        profile.setEducationLevel(EducationLevel.UNIVERSITY);
        profile.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        profile.setMajor(Major.BUSINESS);
        profile.setSpecialty(Specialty.NO_RESTRICTION);

        return profile;
    }
}