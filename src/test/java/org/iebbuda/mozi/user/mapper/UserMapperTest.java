package org.iebbuda.mozi.user.mapper;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.security.account.domain.AuthVO;
import org.iebbuda.mozi.security.account.domain.UserRole;
import org.iebbuda.mozi.security.config.SecurityConfig;
import org.iebbuda.mozi.user.domain.UserVO;
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

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})
@Log4j2
@Transactional
@Rollback
class UserMapperTest {

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserVO testUser;
    private String randomNumber;

    @BeforeEach
    void setUp(){
        randomNumber = UUID.randomUUID().toString().substring(0, 8);
        testUser =createTestUser();
    }

    @Test
    @DisplayName("사용자 등록 테스트")
    void insert() {

        int result = mapper.insert(testUser);
        log.info("result = {}", result);
        assertEquals(1, result);
        assertTrue(testUser.getUserId()>0);
    }

    @Test
    @DisplayName("사용자 ID로 조회 테스트")
    void findByUserId(){

        int result = mapper.insert(testUser);

        UserVO found = mapper.findByUserId(testUser.getUserId());
        log.info("found ={}", found);

        assertEquals(testUser.getLoginId(), found.getLoginId());
        assertNotNull(found.getCreatedAt());
        assertNotNull(found.getUpdatedAt());
    }

    @Test
    @DisplayName("로그인 ID로 조회 테스트")
    void findByLoginId(){

        mapper.insert(testUser);

        //찾는경우
        String loginId1 = testUser.getLoginId();
        UserVO found1 = mapper.findByLoginId(loginId1);
        log.info("found1={}",found1);

        assertNotNull(found1);
        assertEquals(loginId1, found1.getLoginId());

        //못찾는 경우
        String loginId2 ="1q2w3e4r"+randomNumber;
        UserVO found2 = mapper.findByLoginId(loginId2);
        log.info("found2={}", found2);

        assertNull(found2);
    }

    @Test
    @DisplayName("사용자 권한 등록 테스트")
    void insertAuth(){
        mapper.insert(testUser);

        AuthVO auth = new AuthVO(testUser.getUserId(), UserRole.ROLE_USER);
        log.info("auth={}", auth);
        int result = mapper.insertAuth(auth);

        assertEquals(1, result);

    }

    @Test
    @DisplayName("이메일로 로그인ID 찾기")
    void findLoginIdByEmail(){
        mapper.insert(testUser);

        String loginIdByEmail = mapper.findLoginIdByEmail(testUser.getUsername(),testUser.getEmail());

        assertNotNull(loginIdByEmail);
        assertEquals(testUser.getLoginId(), loginIdByEmail);
        log.info("loginId={}", loginIdByEmail);
    }

    @Test
    @DisplayName("전화번호로 로그인ID 찾기")
    void findLoginIdByPhoneNumber(){
        mapper.insert(testUser);

        String loginIdByPhoneNumber = mapper.findLoginIdByPhoneNumber(testUser.getUsername(),testUser.getPhoneNumber());

        assertNotNull(loginIdByPhoneNumber);
        assertEquals(testUser.getLoginId(), loginIdByPhoneNumber);
        log.info("loginId={}", loginIdByPhoneNumber);
    }

    private UserVO createTestUser() {
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

}