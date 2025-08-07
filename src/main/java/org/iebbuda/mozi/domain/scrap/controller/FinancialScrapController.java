package org.iebbuda.mozi.domain.scrap.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.product.dto.DepositResponse;
import org.iebbuda.mozi.domain.product.dto.SavingResponse;
import org.iebbuda.mozi.domain.scrap.dto.DepositScrapDto;
import org.iebbuda.mozi.domain.scrap.dto.FinancialScrapDto;
import org.iebbuda.mozi.domain.scrap.service.DepositScrapService;
import org.iebbuda.mozi.domain.scrap.service.FinancialScrapService;
import org.iebbuda.mozi.domain.scrap.service.SavingScrapService;
import org.iebbuda.mozi.domain.security.account.domain.CustomUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scrap/finance")
@RequiredArgsConstructor
public class FinancialScrapController {

    private final FinancialScrapService financialScrapService;

    // 스크랩 목록 조회
    @GetMapping
    public List<FinancialScrapDto> getUserScraps(@AuthenticationPrincipal CustomUser user) {
        long userId= (long) user.getUser().getUserId();
        System.out.println("userId체크: "+userId);
        return financialScrapService.getUserScraps(userId);
    }

    // 스크랩 추가
    @PostMapping
    public void addScrap(@AuthenticationPrincipal CustomUser user,
                         @RequestParam String productType,
                         @RequestParam Long productId) {
        long userId= (long) user.getUser().getUserId();
        financialScrapService.addScrap(userId, productType, productId);
    }

    // 스크랩 삭제
    @DeleteMapping
    public void removeScrap(@AuthenticationPrincipal CustomUser user,
                            @RequestParam String productType,
                            @RequestParam Long productId) {
        long userId= (long) user.getUser().getUserId();
        financialScrapService.removeScrap(userId, productType, productId);
    }
}
