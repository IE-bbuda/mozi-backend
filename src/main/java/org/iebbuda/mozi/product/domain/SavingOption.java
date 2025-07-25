package org.iebbuda.mozi.product.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SavingOption {
    private Long optionId;
    private Long savingId; //FK
    private String intrRateType;
    private String intrRateTypeNm;
    private String rsrvType;
    private String rsrvTypeNm;
    private int saveTrm;
    private BigDecimal intrRate;
    private BigDecimal intrRate2;
}
