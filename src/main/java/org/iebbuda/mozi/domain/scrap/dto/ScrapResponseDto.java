package org.iebbuda.mozi.domain.scrap.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScrapResponseDto {
    private Long scrapId;
    private String productType; // "DEPOSIT" or "SAVING"
    private Object product;     // DepositResponse 또는 SavingResponse
}
