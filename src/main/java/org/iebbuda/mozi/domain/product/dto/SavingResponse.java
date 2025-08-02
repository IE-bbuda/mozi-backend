package org.iebbuda.mozi.domain.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private LocalDate disclosureStartDate;
    private LocalDate disclosureEndDate;

    private List<SavingOptionResponse> options;

}
