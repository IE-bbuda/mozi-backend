package org.iebbuda.mozi.domain.product.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DepositProduct {
    private Long depositId;
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

    private List<DepositOption> options=new ArrayList<>();
}
