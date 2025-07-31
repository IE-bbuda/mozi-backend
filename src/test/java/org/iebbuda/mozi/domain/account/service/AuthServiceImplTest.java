package org.iebbuda.mozi.domain.account.service;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.account.external.ExternalApiClientConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class, ExternalApiClientConfig.class })
@Log4j2
class AuthServiceImplTest {
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthService authService;
    @Test
    void getAccessToken() {
        String accessToken=authService.getAccessToken();
        System.out.println("발급받은 토큰: " + accessToken);
    }
}