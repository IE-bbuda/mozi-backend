package org.iebbuda.mozi.domain.scrap.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FinancialScrapDto {
    private Long scrapId;
    private Long userId;
    private LocalDateTime createdAt;
    private String productType; // DEPOSIT or SAVING
    private Object product;     // DepositResponse or SavingResponse

    public static FinancialScrapDto fromDeposit(DepositScrapDto depositScrap) {
        return FinancialScrapDto.builder()
                .scrapId(depositScrap.getScrapId())
                .userId(depositScrap.getUserId())
                .createdAt(depositScrap.getCreatedAt())
                .productType("DEPOSIT")
                .product(depositScrap.getDeposit())
                .build();
    }

    public static FinancialScrapDto fromSaving(SavingScrapDto savingScrap) {
        return FinancialScrapDto.builder()
                .scrapId(savingScrap.getScrapId())
                .userId(savingScrap.getUserId())
                .createdAt(savingScrap.getCreatedAt())
                .productType("SAVING")
                .product(savingScrap.getSaving())
                .build();
    }
}
