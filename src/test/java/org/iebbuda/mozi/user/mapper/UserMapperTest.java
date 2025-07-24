package org.iebbuda.mozi.user.mapper;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.security.account.domain.AuthVO;
import org.iebbuda.mozi.security.account.domain.UserRole;
import org.iebbuda.mozi.security.config.SecurityConfig;
import org.iebbuda.mozi.user.domain.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
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



    @Test
    void insert() {
        UserVO user = createTestUser();

        int result = mapper.insert(user);
        log.info("result = {}", result);
        assertEquals(1, result);
        assertTrue(user.getUserId()>0);
    }

    @Test
    void findByUserId(){
        UserVO user = createTestUser();
        int result = mapper.insert(user);

        UserVO found = mapper.findByUserId(user.getUserId());
        log.info("found ={}", found);

        assertEquals(user.getLoginId(), found.getLoginId());
        assertNotNull(found.getCreateAt());
        assertNotNull(found.getUpdateAt());
    }

    @Test
    void findByLoginId(){
        UserVO user = createTestUser();
        mapper.insert(user);

        //찾는경우
        String loginId1 = user.getLoginId();
        UserVO found1 = mapper.findByLoginId(loginId1);
        log.info("found1={}",found1);

        assertNotNull(found1);
        assertEquals(loginId1, found1.getLoginId());

        //못찾는 경우
        String loginId2 ="1q2w3e4r"+UUID.randomUUID().toString().substring(0, 8);
        UserVO found2 = mapper.findByLoginId(loginId2);
        log.info("found2={}", found2);

        assertNull(found2);
    }

    @Test
    void insertAuth(){
        UserVO user = createTestUser();
        mapper.insert(user);

        AuthVO auth = new AuthVO(user.getUserId(), UserRole.ROLE_USER);
        log.info("auth={}", auth);
        int result = mapper.insertAuth(auth);

        assertEquals(1, result);

    }


    private UserVO createTestUser() {
        String randomNumber = UUID.randomUUID().toString().substring(0, 8);

        UserVO user = UserVO.builder()
                .username("테스트유저" + randomNumber)
                .loginId("testuser" + randomNumber)
                .password(passwordEncoder.encode("password123"))
                .phoneNumber("010-1234-5678")
                .email("test" + randomNumber + "@email.com")
                .birthDate(Date.valueOf("1990-01-15"))
                .build();
        return user;
    }

}