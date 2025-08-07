package org.iebbuda.mozi.domain.scrap.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.scrap.service.ScrapService;
import org.iebbuda.mozi.domain.security.account.domain.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scrap")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrabService;
    // 스크랩된 정책 plcyNo 목록 조회
    @GetMapping
    public ResponseEntity<List<String>> getScrapedList(@AuthenticationPrincipal CustomUser user) {
        int userId = user.getUser().getUserId();
        return ResponseEntity.ok(scrabService.getScrapedPolicyNos(userId));
    }

    // 정책 스크랩 등록
    @PostMapping("/policy/{plcyNo}")
    public ResponseEntity<Void> scrapPolicy(@AuthenticationPrincipal CustomUser user,
                                            @PathVariable String plcyNo) {
        int userId = user.getUser().getUserId();
        scrabService.scrapPolicy(userId, plcyNo);
        return ResponseEntity.ok().build();
    }

    // 정책 스크랩 취소
    @DeleteMapping("/policy/{plcyNo}")
    public ResponseEntity<Void> cancelScrap(@AuthenticationPrincipal CustomUser user,
                                            @PathVariable String plcyNo) {
        int userId = user.getUser().getUserId();
        scrabService.cancelScrapPolicy(userId, plcyNo);
        return ResponseEntity.ok().build();
    }

    // 정책이 스크랩되어 있는지 확인
    @GetMapping("/policy/{plcyNo}/check")
    public ResponseEntity<Boolean> checkScrap(@AuthenticationPrincipal CustomUser user,
                                              @PathVariable String plcyNo) {
        int userId = user.getUser().getUserId();
        return ResponseEntity.ok(scrabService.isScrapedPolicy(userId, plcyNo));
    }

    // 스크랩된 정책 전체 조회
    @GetMapping("/policy/list")
    public ResponseEntity<List<PolicyVO>> getScrapedPolicies(@AuthenticationPrincipal CustomUser user) {
        int userId = user.getUser().getUserId();
        System.out.println("🔥 getScrappedPolicies 요청됨 - userId: " + userId);
        return ResponseEntity.ok(scrabService.getScrapedPolicies(userId));
    }
//
//    // 스크랩된 정책 plcyNo 목록 조회
//    @GetMapping
//    public ResponseEntity<List<String>> getScrapedList(@RequestParam int userId) {
//        return ResponseEntity.ok(scrabService.getScrapedPolicyNos(userId));
//    }
//
//    // 정책 스크랩 등록
//    @PostMapping("/policy/{plcyNo}")
//    public ResponseEntity<Void> scrapPolicy(@RequestParam int userId, @PathVariable String plcyNo) {
//        scrabService.scrapPolicy(userId, plcyNo);
//        return ResponseEntity.ok().build();
//    }
//
//    // 정책 스크랩 취소
//    @DeleteMapping("/policy/{plcyNo}")
//    public ResponseEntity<Void> cancelScrap(@RequestParam int userId, @PathVariable String plcyNo) {
//        scrabService.cancelScrapPolicy(userId, plcyNo);
//        return ResponseEntity.ok().build();
//    }
//
//    // 정책이 스크랩되어 있는지 확인
//    @GetMapping("/policy/{plcyNo}/check")
//    public ResponseEntity<Boolean> checkScrap(@RequestParam int userId, @PathVariable String plcyNo) {
//        return ResponseEntity.ok(scrabService.isScrapedPolicy(userId, plcyNo));
//    }
//
//    // 스크랩된 정책 전체 조회
//    @GetMapping("/policy/list")
//    public ResponseEntity<List<PolicyVO>> getScrapedPolicies(@RequestParam int userId) {
//        System.out.println("🔥 getScrappedPolicies 요청됨 - userId: " + userId);
//        return ResponseEntity.ok(scrabService.getScrapedPolicies(userId));
//    }
}
