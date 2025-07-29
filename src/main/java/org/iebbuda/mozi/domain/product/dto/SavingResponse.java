package org.iebbuda.mozi.domain.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class SavingResponse {
    private Long savingId;
    private String bankCode;
    private String bankName;
    private String productName;
    private String joinWay;
    private String joinDeny;
    private String joinMember;
    private String specialCondition;
    private String etcNote;
    private BigDecimal maxLimit;
    private String disclosureMonth;
    private String disclosureStartDate;
    private String disclosureEndDate;

    private List<OptionResponse> options;

    @Data
    @Builder
    public static class OptionResponse {
        private String intrRateType;
        private String intrRateTypeNm;
        private String rsrvType;
        private String rsrvTypeNm;
        private int saveTrm;
        private BigDecimal intrRate;
        private BigDecimal intrRate2;
    }

}
