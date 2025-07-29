package org.iebbuda.mozi.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.product.domain.SavingOption;
import org.iebbuda.mozi.domain.product.domain.SavingProduct;
import org.iebbuda.mozi.domain.product.dto.SavingResponse;
import org.iebbuda.mozi.domain.product.mapper.SavingMapper;
import org.iebbuda.mozi.domain.product.mapper.SavingOptionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingQueryService {

    private final SavingMapper savingMapper;
    private final SavingOptionMapper savingOptionMapper;

    /**
     * 모든 적금 상품 반환
     */
    public List<SavingResponse>getAllSavings(){
        List<SavingProduct>products=savingMapper.findAll();

        return products.stream()
                .map(product->{
                    List<SavingOption>options=savingOptionMapper.findByProductId(product.getSavingId());

                    return toResponse(product,options);
                })
                .toList();
    }

    /**
     * 모든 적금 상품 반환
     */
    public SavingResponse getSavingById(Long id){
        SavingProduct product=savingMapper.findById(id);
        if(product==null){
            throw new RuntimeException("적금 상품을 찾을 수 없습니다. (id=" + id + ")");
        }
        List<SavingOption>options=savingOptionMapper.findByProductId(product.getSavingId());
        return toResponse(product,options);
    }
    private SavingResponse toResponse(SavingProduct product, List<SavingOption>options){
        return SavingResponse.builder()
                .savingId(product.getSavingId())
                .bankCode(product.getFinCoNo())
                .bankName(product.getKorCoNm())
                .productName(product.getFinPrdtNm())
                .joinWay(product.getJoinWay())
                .joinDeny(product.getJoinDeny())
                .joinMember(product.getJoinMember())
                .specialCondition(product.getSpclCnd())
                .etcNote(product.getEtcNote())
                .maxLimit(product.getMaxLimit())
                .disclosureMonth(formatMonth(product.getDclsMonth()))
                .disclosureStartDate(formatDate(product.getDclsStrtDay()))
                .disclosureEndDate(formatDate(product.getDclsEndDay()))
                .options(options.stream()
                        .map(opt->SavingResponse.OptionResponse.builder()
                                .intrRateType(opt.getIntrRateType())
                                .intrRateTypeNm(opt.getIntrRateTypeNm())
                                .rsrvType(opt.getRsrvType())
                                .rsrvTypeNm(opt.getRsrvTypeNm())
                                .saveTrm(opt.getSaveTrm())
                                .intrRate(opt.getIntrRate())
                                .intrRate2(opt.getIntrRate2())
                                .build())
                        .toList())
                .build();
    }

    private String formatMonth(String yyyyMM) {
        return yyyyMM.substring(0, 4) + "-" + yyyyMM.substring(4, 6);
    }

    private String formatDate(LocalDate date) {
        return (date != null) ? date.toString() : null;
    }
}
