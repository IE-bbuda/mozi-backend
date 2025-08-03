package org.iebbuda.mozi.domain.scrap.converter;

import org.iebbuda.mozi.domain.product.dto.SavingOptionResponse;
import org.iebbuda.mozi.domain.product.dto.SavingResponse;
import org.iebbuda.mozi.domain.scrap.domain.SavingScrap;
import org.iebbuda.mozi.domain.scrap.dto.SavingScrapDto;
import org.springframework.stereotype.Component;

@Component
public class SavingScrapConverter {

    public SavingScrapDto toResponse(SavingScrap scrap) {
        return SavingScrapDto.builder()
                .scrapId(scrap.getScrapId())
                .userId(scrap.getUserId())
                .createdAt(scrap.getCreatedAt())
                .saving(SavingResponse.builder()
                        .savingId(scrap.getSavingProduct().getSavingId())
                        .bankCode(scrap.getSavingProduct().getFinCoNo())
                        .bankName(scrap.getSavingProduct().getKorCoNm())
                        .productName(scrap.getSavingProduct().getFinPrdtNm())
                        .joinWay(scrap.getSavingProduct().getJoinWay())
                        .joinDeny(scrap.getSavingProduct().getJoinDeny())
                        .joinMember(scrap.getSavingProduct().getJoinMember())
                        .specialCondition(scrap.getSavingProduct().getSpclCnd())
                        .etcNote(scrap.getSavingProduct().getEtcNote())
                        .maxLimit(scrap.getSavingProduct().getMaxLimit())
                        .disclosureMonth(scrap.getSavingProduct().getDclsMonth())
                        .disclosureStartDate(scrap.getSavingProduct().getDclsStrtDay())
                        .disclosureEndDate(scrap.getSavingProduct().getDclsEndDay())
                        .options(scrap.getSavingProduct().getOptions().stream()
                                .map(o -> SavingOptionResponse.builder()
                                        .intrRateType(o.getIntrRateType())
                                        .intrRateTypeNm(o.getIntrRateTypeNm())
                                        .rsrvType(o.getRsrvType())
                                        .rsrvTypeNm(o.getRsrvTypeNm())
                                        .saveTrm(o.getSaveTrm())
                                        .intrRate(o.getIntrRate())
                                        .intrRate2(o.getIntrRate2())
                                        .build())
                                .toList())
                        .build())
                .build();
    }
}
