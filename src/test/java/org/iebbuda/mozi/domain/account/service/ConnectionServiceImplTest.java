package org.iebbuda.mozi.domain.account.service;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.account.dto.BankLoginRequestDTO;
import org.iebbuda.mozi.domain.account.external.ExternalApiClientConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class, ExternalApiClientConfig.class })
@Log4j2
public class ConnectionServiceImplTest {

    @Autowired
    ConnectionService connectionService;
    @Test
    void connectTest() {

        BankLoginRequestDTO dto= BankLoginRequestDTO.builder()
                .bankCode("0004")
                .userBankId("") //아이디 채우기
                .userBankPassword("")//비밀번호 채우기
                .build();

        String connectionId = connectionService.connect(dto);

        System.out.println("connectionId = " + connectionId);
        assertNotNull(connectionId);
    }
}