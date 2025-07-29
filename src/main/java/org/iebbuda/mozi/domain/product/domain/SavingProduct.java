package org.iebbuda.mozi.domain.product.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SavingProduct {
    private Long savingId;
    private String finPrdtCd;
    private String finCoNo;
    private String korCoNm;
    private String finPrdtNm;
    private String joinWay;
    private String joinDeny;
    private String joinMember;
    private String spclCnd;
    private String etcNote;
    private BigDecimal maxLimit;
    private String dclsMonth;
    private LocalDate dclsStrtDay;
    private LocalDate dclsEndDay;
    private LocalDateTime finCoSubmDay;
}
