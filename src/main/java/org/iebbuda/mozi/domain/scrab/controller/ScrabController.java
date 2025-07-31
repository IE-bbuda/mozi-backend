package org.iebbuda.mozi.domain.scrab.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.scrab.mapper.PolicyScrabMapper;
import org.iebbuda.mozi.domain.scrab.service.ScrabService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/scrap")
@RequiredArgsConstructor
public class ScrabController {

    private final ScrabService scrabService;

    // 정책 스크랩 등록
    @PostMapping("/policy/{policyId}")
    public ResponseEntity<Void> scrapPolicy(@RequestParam int userId, @PathVariable int policyId) {
        scrabService.scrabPolicy(userId, policyId);
        return ResponseEntity.ok().build();
    }

    // 정책 스크랩 취소
    @DeleteMapping("/policy/{policyId}")
    public ResponseEntity<Void> cancelScrap(@RequestParam int userId, @PathVariable int policyId) {
        scrabService.cancelScrabPolicy(userId, policyId);
        return ResponseEntity.ok().build();
    }

    // 정책이 스크랩되어 있는지 확인
    @GetMapping("/policy/{policyId}/check")
    public ResponseEntity<Boolean> checkScrap(@RequestParam int userId, @PathVariable int policyId) {
        return ResponseEntity.ok(scrabService.isScrabbedPolicy(userId, policyId));
    }

    // 사용자의 스크랩된 정책 ID 목록 조회
    @GetMapping
    public ResponseEntity<List<Integer>> getScrabbedList(@RequestParam int userId) {
        return ResponseEntity.ok(scrabService.getScrabbedPolicyIds(userId));
    }
}
