package org.iebbuda.mozi.domain.product.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositOption {
    private Long optionId;
    private Long depositId; //FK
    private String intrRateType;
    private String intrRateTypeNm;
    private int saveTrm;
    private BigDecimal intrRate;
    private BigDecimal intrRate2;
}
