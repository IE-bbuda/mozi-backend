package org.iebbuda.mozi.domain.scrap.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.scrap.service.ScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scrap")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrabService;

    // 사용자의 스크랩된 정책 ID 목록 조회
    @GetMapping
    public ResponseEntity<List<Integer>> getScrapedList(@RequestParam int userId) {
        return ResponseEntity.ok(scrabService.getScrapedPolicyIds(userId));
    }

    // 정책 스크랩 등록
    @PostMapping("/policy/{policyId}")
    public ResponseEntity<Void> scrapPolicy(@RequestParam int userId, @PathVariable int policyId) {
        scrabService.scrapPolicy(userId, policyId);
        return ResponseEntity.ok().build();
    }

    // 정책 스크랩 취소
    @DeleteMapping("/policy/{policyId}")
    public ResponseEntity<Void> cancelScrap(@RequestParam int userId, @PathVariable int policyId) {
        scrabService.cancelScrapPolicy(userId, policyId);
        return ResponseEntity.ok().build();
    }

    // 정책이 스크랩되어 있는지 확인
    @GetMapping("/policy/{policyId}/check")
    public ResponseEntity<Boolean> checkScrap(@RequestParam int userId, @PathVariable int policyId) {
        return ResponseEntity.ok(scrabService.isScrapedPolicy(userId, policyId));
    }

    @GetMapping("/policy/list")
    public ResponseEntity<List<PolicyVO>> getScrapedPolicies(@RequestParam int userId) {
        System.out.println("🔥 getScrappedPolicies 요청됨 - userId: " + userId); // 로그 추가
        return ResponseEntity.ok(scrabService.getScrapedPolicies(userId));
    }
}
