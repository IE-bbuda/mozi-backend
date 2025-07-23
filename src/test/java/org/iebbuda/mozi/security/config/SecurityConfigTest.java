package org.iebbuda.mozi.security.config;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        RootConfig.class,
        SecurityConfig.class
})
@Log4j2
class SecurityConfigTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testEncode(){

        String str1 = "1234";
        String enStr1 = passwordEncoder.encode(str1);
        String str2="1234";
        String enStr2 = passwordEncoder.encode(str2);

        String str3="1324";
        log.info("enStr ={}", enStr1);
        log.info("enStr ={}", enStr2);
        assertNotEquals(str1,enStr1);
        assertNotEquals(enStr1,enStr2);
        assertTrue(passwordEncoder.matches(str1, enStr1));
        assertFalse(passwordEncoder.matches(str3, enStr1));
    }
}