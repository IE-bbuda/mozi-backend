package org.iebbuda.mozi.domain.scrap.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.product.dto.DepositResponse;
import org.iebbuda.mozi.domain.product.dto.SavingResponse;
import org.iebbuda.mozi.domain.scrap.dto.DepositScrapDto;
import org.iebbuda.mozi.domain.scrap.dto.FinancialScrapDto;
import org.iebbuda.mozi.domain.scrap.service.DepositScrapService;
import org.iebbuda.mozi.domain.scrap.service.FinancialScrapService;
import org.iebbuda.mozi.domain.scrap.service.SavingScrapService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scrap/finance")
@RequiredArgsConstructor
public class FinancialScrapController {

    private final FinancialScrapService financialScrapService;

    // 스크랩 목록 조회
    @GetMapping
    public List<FinancialScrapDto> getUserScraps(@RequestParam Long userId) {
        return financialScrapService.getUserScraps(userId);
    }

    // 스크랩 추가
    @PostMapping
    public void addScrap(@RequestParam Long userId,
                         @RequestParam String productType,
                         @RequestParam Long productId) {
        financialScrapService.addScrap(userId, productType, productId);
    }

    // 스크랩 삭제
    @DeleteMapping
    public void removeScrap(@RequestParam Long userId,
                            @RequestParam String productType,
                            @RequestParam Long productId) {
        financialScrapService.removeScrap(userId, productType, productId);
    }
}
