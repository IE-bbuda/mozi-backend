package org.iebbuda.mozi.domain.account.service;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.account.domain.AccountVO;
import org.iebbuda.mozi.domain.account.dto.BankLoginRequestDTO;
import org.iebbuda.mozi.domain.account.external.ExternalApiClientConfig;
import org.iebbuda.mozi.domain.account.mapper.AccountMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class, ExternalApiClientConfig.class })
@Log4j2
class AccountServiceImplTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountMapper accountMapper;


    @Test
    void fetchAccounts() {
        List<AccountVO> list=accountService.fetchAccounts(
                "0004", "1pweGvU1ATP8CHlOZcAewq");
        list.forEach(System.out::println);
    }

    @Test
    void addAccounts() {
        BankLoginRequestDTO req1=BankLoginRequestDTO.builder()
                .bankCode("0011").userBankId("").userBankPassword("").build();
        BankLoginRequestDTO req2=BankLoginRequestDTO.builder()
                .bankCode("0004").userBankId("").userBankPassword("").build();
        List<BankLoginRequestDTO> reqList=new ArrayList<>();
        reqList.add(req1);
        reqList.add(req2);
        boolean s1=accountService.addAccounts(req1,1);
        //list.forEach(System.out::println);
        boolean s2=accountService.addAccounts(req2,1);
        //list2.forEach(System.out::println);
//        System.out.println(s1+" "+s2);
    }

    @Test
    void refreshAccounts() {
        Map<String, Object> result=accountService.refreshAccounts(1);
        System.out.println(result);
    }

    @Test
    void deleteAccounts() {
        Map<String,Object> result=accountService.deleteAccounts(List.of("0011", "0004"),1);
        System.out.println("=============================================================");
        System.out.println(result);

    }

    @Test
    void getBankSummary() {
        Map<String, Object> result=accountService.getBankSummary(1);
        log.info("result list"+result);
    }

    @Test
    void getAccountsByBank() {
        Map<String, Object> list=accountService.getAccountsByBank("0004", 1);
        System.out.println(list);
        Map<String, Object> list2=accountService.getAccountsByBank("0011", 1);
        System.out.println(list2);
    }

    @Test
    void getMainBankSummary() {
        Map<String, Object> result=accountService.getMainBankSummary(1);
        System.out.println(result);
    }

    @Test
    void getAccounts() {
        Map<String, Object> result=accountService.getAccounts(1);
        System.out.println(result);
    }

    @Test
    void updateAccountsByGoal() {
        Map<String, Object> result=accountService.updateAccountsByGoal(List.of("854703-01-194182","1111-11"), 1, 1);
    }
}