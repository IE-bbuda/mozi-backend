package org.iebbuda.mozi.domain.scrap.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinancialScrapDto {
    private Long scrapId;
    private Long userId;
    private String productType; // DEPOSIT or SAVING
    private Object product;     // DepositResponse or SavingResponse

    public static FinancialScrapDto fromDeposit(DepositScrapDto depositScrap) {
        return FinancialScrapDto.builder()
                .scrapId(depositScrap.getScrapId())
                .userId(depositScrap.getUserId())
                .productType("DEPOSIT")
                .product(depositScrap.getDeposit())
                .build();
    }

    public static FinancialScrapDto fromSaving(SavingScrapDto savingScrap) {
        return FinancialScrapDto.builder()
                .scrapId(savingScrap.getScrapId())
                .userId(savingScrap.getUserId())
                .productType("SAVING")
                .product(savingScrap.getSaving())
                .build();
    }
}
