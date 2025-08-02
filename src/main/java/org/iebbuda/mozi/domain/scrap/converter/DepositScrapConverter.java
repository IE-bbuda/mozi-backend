package org.iebbuda.mozi.domain.scrap.converter;

import org.iebbuda.mozi.domain.product.dto.DepositOptionResponse;
import org.iebbuda.mozi.domain.product.dto.DepositResponse;
import org.iebbuda.mozi.domain.scrap.domain.DepositScrap;
import org.iebbuda.mozi.domain.scrap.dto.DepositScrapDto;
import org.springframework.stereotype.Component;

@Component
public class DepositScrapConverter {

    public DepositScrapDto toResponse(DepositScrap scrap) {
        return DepositScrapDto.builder()
                .scrapId(scrap.getScrapId())
                .userId(scrap.getUserId())
                .createdAt(scrap.getCreatedAt())
                .deposit(DepositResponse.builder()
                        .depositId(scrap.getDepositProduct().getDepositId())
                        .bankCode(scrap.getDepositProduct().getFinCoNo())
                        .bankName(scrap.getDepositProduct().getKorCoNm())
                        .productName(scrap.getDepositProduct().getFinPrdtNm())
                        .joinWay(scrap.getDepositProduct().getJoinWay())
                        .joinDeny(scrap.getDepositProduct().getJoinDeny())
                        .joinMember(scrap.getDepositProduct().getJoinMember())
                        .specialCondition(scrap.getDepositProduct().getSpclCnd())
                        .etcNote(scrap.getDepositProduct().getEtcNote())
                        .maxLimit(scrap.getDepositProduct().getMaxLimit())
                        .disclosureMonth(scrap.getDepositProduct().getDclsMonth())
                        .disclosureStartDate(scrap.getDepositProduct().getDclsStrtDay())
                        .disclosureEndDate(scrap.getDepositProduct().getDclsEndDay())
                        .options(scrap.getDepositProduct().getOptions().stream()
                                .map(o -> DepositOptionResponse.builder()
                                        .intrRateType(o.getIntrRateType())
                                        .intrRateTypeNm(o.getIntrRateTypeNm())
                                        .saveTrm(o.getSaveTrm())
                                        .intrRate(o.getIntrRate())
                                        .intrRate2(o.getIntrRate2())
                                        .build())
                                .toList())
                        .build())
                .build();
    }
}