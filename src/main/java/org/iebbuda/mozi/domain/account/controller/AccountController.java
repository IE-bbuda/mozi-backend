package org.iebbuda.mozi.domain.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.account.domain.AccountVO;
import org.iebbuda.mozi.domain.account.service.AccountService;
import org.iebbuda.mozi.domain.account.dto.BankLoginRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;


    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getAccounts(){
        Integer userId=1;
        Map<String, Object> result=accountService.getAccounts(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-by-goal")
    public ResponseEntity<Map<String, Object>> getAccountsByGoal(
            @RequestParam Integer goalId
    ){
        Integer userId=1;
        Map<String, Object> result=accountService.getAccountsByGoal(goalId,userId);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addAccounts(
            @RequestBody BankLoginRequestDTO bankLoginRequestDTO
    ){
        Integer userId=1; //수정
        boolean success=accountService.addAccounts(bankLoginRequestDTO,userId);
        return ResponseEntity.ok(Map.of("success",success));
    }
    @DeleteMapping("/delete")
    public ResponseEntity<List<String>> deleteAccounts(
            @RequestBody List<String> bankCodeList
    ){
        Integer userId=1;
        List<String> deletedBankCode=accountService.deleteAccounts(bankCodeList, userId);
        return ResponseEntity.ok(deletedBankCode);
    }

    @GetMapping("/mainsummary")
    public ResponseEntity<Map<String, Object>> getMainBankSummary(
    ){
        Integer userId=1;
        Map<String, Object> result=accountService.getMainBankSummary(userId);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getBankSummary(
    ){
        Integer userId=1;
        Map<String, Object> list=accountService.getBankSummary(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/get-by-bank")
    public ResponseEntity<Map<String, Object>> getAccountsByBank(
            @RequestParam String bankCode
    ){
        Integer userId=1;
        Map<String, Object> list=accountService.getAccountsByBank(bankCode, userId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/refresh")
    public ResponseEntity<List<AccountVO>> refreshAccounts(
    ){
        Integer userId=1;
        List<AccountVO> accounts=accountService.refreshAccounts(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/get-mainbank")
    public ResponseEntity<Map<String, Object>> getBank(){
        Integer userId=1;
        Map<String, Object> result=accountService.getBank(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/set-mainbank")
    public ResponseEntity<Map<String, Object>> setMainBank(@RequestBody Map<String, String> body) {
        Integer userId = 1;
        String bankCode = body.get("bankCode");
        Map<String, Object> result=accountService.updateMainBankCode(bankCode, userId);
        return ResponseEntity.ok(result);
    }


}
