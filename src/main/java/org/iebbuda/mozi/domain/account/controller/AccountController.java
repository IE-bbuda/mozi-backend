package org.iebbuda.mozi.domain.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.account.domain.AccountVO;
import org.iebbuda.mozi.domain.account.service.AccountService;
import org.iebbuda.mozi.domain.account.dto.BankLoginRequestDTO;
import org.iebbuda.mozi.domain.security.account.domain.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
public ResponseEntity<?> getAccounts(@AuthenticationPrincipal CustomUser user) {
    try {
        Integer userId = user.getUser().getUserId();
        Map<String, Object> result = accountService.getAccounts(userId);
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        log.error("계좌 조회 중 오류 발생", e);  // 예외와 함께 로그 남기기
        return ResponseEntity.status(500).body(Map.of("error", "서버 오류가 발생했습니다."));
    }
}


    @GetMapping("/get-by-goal")
    public ResponseEntity<Map<String, Object>> getAccountsByGoal(
            @RequestParam Integer goalId,
            @AuthenticationPrincipal CustomUser user
    ){
        Integer userId = user.getUser().getUserId();
        Map<String, Object> result=accountService.getAccountsByGoal(goalId,userId);
        return ResponseEntity.ok(result);
    }


@PostMapping("/add")
public ResponseEntity<Map<String, Object>> addAccounts(
        @RequestBody BankLoginRequestDTO bankLoginRequestDTO,
        @AuthenticationPrincipal CustomUser user
){
    if (user == null) {
        log.error("AuthenticationPrincipal로부터 CustomUser를 받지 못했습니다.");
        return ResponseEntity.status(401).body(Map.of("success", false, "error", "인증되지 않은 사용자입니다."));
    }
    int userId = user.getUser().getUserId();
    log.info("요청한 userId: {}", userId);
    try {
        boolean success = accountService.addAccounts(bankLoginRequestDTO, userId);
        log.info("계좌 추가 성공: {}", success);
        return ResponseEntity.ok(Map.of("success", success));
    } catch (Exception e) {
        log.error("계좌 추가 실패: ", e);
        return ResponseEntity.status(500).body(Map.of("success", false, "error", e.getMessage()));
    }
}


    @DeleteMapping("/delete")
    public ResponseEntity<List<String>> deleteAccounts(
            @RequestBody List<String> bankCodeList,
            @AuthenticationPrincipal CustomUser user
    ){
        Integer userId=user.getUser().getUserId();
        List<String> deletedBankCode=accountService.deleteAccounts(bankCodeList, userId);
        return ResponseEntity.ok(deletedBankCode);
    }

    @GetMapping("/mainsummary")
    public ResponseEntity<Map<String, Object>> getMainBankSummary(@AuthenticationPrincipal CustomUser user
    ){
        Integer userId=user.getUser().getUserId();
        Map<String, Object> result=accountService.getMainBankSummary(userId);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getBankSummary(@AuthenticationPrincipal CustomUser user
    ){
        Integer userId=user.getUser().getUserId();
        Map<String, Object> list=accountService.getBankSummary(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/get-by-bank")
    public ResponseEntity<Map<String, Object>> getAccountsByBank(
            @RequestParam String bankCode,
            @AuthenticationPrincipal CustomUser user
    ){
        Integer userId=user.getUser().getUserId();
        Map<String, Object> list=accountService.getAccountsByBank(bankCode, userId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/refresh")
    public ResponseEntity<List<AccountVO>> refreshAccounts(@AuthenticationPrincipal CustomUser user
    ){
        Integer userId=user.getUser().getUserId();
        List<AccountVO> accounts=accountService.refreshAccounts(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/get-mainbank")
    public ResponseEntity<Map<String, Object>> getBank(@AuthenticationPrincipal CustomUser user){
        Integer userId=user.getUser().getUserId();
        Map<String, Object> result=accountService.getBank(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/set-mainbank")
    public ResponseEntity<Map<String, Object>> setMainBank(@RequestBody Map<String, String> body,@AuthenticationPrincipal CustomUser user) {
        Integer userId=user.getUser().getUserId();
        String bankCode = body.get("bankCode");
        Map<String, Object> result=accountService.updateMainBankCode(bankCode, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/update-by-goal")
    public ResponseEntity<Map<String, Object>> updateAccountsByGoal(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CustomUser user
    ){
        Integer userId=user.getUser().getUserId();
        Integer goalId=Integer.parseInt(body.get("goalId").toString());
        List<String> accountNumberList=(List<String>) body.get("accountNumberList");
        Map<String, Object> result=accountService.updateAccountsByGoal(accountNumberList, goalId, userId);
        return ResponseEntity.ok(result);
    }
}
