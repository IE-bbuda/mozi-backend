package org.iebbuda.mozi.domain.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SavingOptionResponse {
    private String intrRateType;
    private String intrRateTypeNm;
    private String rsrvType;
    private String rsrvTypeNm;
    private int saveTrm;
    private BigDecimal intrRate;
    private BigDecimal intrRate2;
}
