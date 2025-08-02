package org.iebbuda.mozi.domain.scrap.service;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.scrap.dto.FinancialScrapDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FinancialScrapService {

    private final DepositScrapService depositScrapService;
    private final SavingScrapService savingScrapService;

    public List<FinancialScrapDto> getUserScraps(Long userId) {
        var deposits = depositScrapService.getUserScraps(userId)
                .stream()
                .map(FinancialScrapDto::fromDeposit)
                .toList();

        var savings = savingScrapService.getUserScraps(userId)
                .stream()
                .map(FinancialScrapDto::fromSaving)
                .toList();

        return Stream.concat(deposits.stream(), savings.stream()).toList();
    }

    public void addScrap(Long userId, String productType, Long productId) {
        if ("DEPOSIT".equalsIgnoreCase(productType)) {
            depositScrapService.addScrap(userId, productId);
        } else if ("SAVING".equalsIgnoreCase(productType)) {
            savingScrapService.addScrap(userId, productId);
        } else {
            throw new IllegalArgumentException("잘못된 productType: " + productType);
        }
    }

    public void removeScrap(Long userId, String productType, Long productId) {
        if ("DEPOSIT".equalsIgnoreCase(productType)) {
            depositScrapService.removeScrap(userId, productId);
        } else if ("SAVING".equalsIgnoreCase(productType)) {
            savingScrapService.removeScrap(userId, productId);
        } else {
            throw new IllegalArgumentException("잘못된 productType: " + productType);
        }
    }
}
