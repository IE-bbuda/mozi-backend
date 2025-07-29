package org.iebbuda.mozi.domain.security.util;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.security.config.SecurityConfig;
import org.iebbuda.mozi.domain.security.util.JwtProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})
@Log4j2
class JwtProcessorTest {

    @Autowired
    JwtProcessor jwtProcessor;

    @Test
    void generateToken() {
        String username = "user0";
        String token = jwtProcessor.generateToken(username);
        log.info("token = {}", token);
        assertNotNull(token);
    }

//    @Test
//    void getUsername() {
//        //시간 경과 후 테스트
//        String token = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMCIsImlhdCI6MTc1MzI0NzcwMiwiZXhwIjoxNzUzMjQ4MDAyfQ.YsQj_HJo-tTLDpSVzxz2RlDy4zq6XDxe9jaoUTmB6Msx7jxqHSb2N84HcTFQzcaI";
//
//        String loginId = jwtProcessor.getLoginId(token);
//        log.info("loginId ={}", loginId);
//        assertNotNull(loginId);
//    }
}