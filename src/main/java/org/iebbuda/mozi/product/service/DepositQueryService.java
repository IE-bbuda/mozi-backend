package org.iebbuda.mozi.product.service;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.product.domain.DepositOption;
import org.iebbuda.mozi.product.domain.DepositProduct;
import org.iebbuda.mozi.product.dto.DepositResponse;
import org.iebbuda.mozi.product.mapper.DepositMapper;
import org.iebbuda.mozi.product.mapper.DepositOptionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositQueryService {

    private final DepositMapper depositMapper;
    private final DepositOptionMapper optionMapper;

    /**
     * 모든 예금 상품 반환
     */
    public List<DepositResponse> getAllDeposits() {
        List<DepositProduct> products = depositMapper.findAll();

        return products.stream()
                .map(product -> {
                    List<DepositOption> options = optionMapper.findByProductId(product.getDepositId());

                    return toResponse(product, options);
                })
                .toList();
    }

    /**
     * 단일 예금 상품 반환
     */
    public DepositResponse getDepositById(Long id) {
        DepositProduct product = depositMapper.findById(id);
        if (product == null) {
            throw new RuntimeException("예금 상품을 찾을 수 없습니다. (id=" + id + ")");
        }
        List<DepositOption> options = optionMapper.findByProductId(product.getDepositId());
        return toResponse(product, options);
    }

    private DepositResponse toResponse(DepositProduct product, List<DepositOption> options) {
        return DepositResponse.builder()
                .depositId(product.getDepositId())
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
                        .map(opt -> DepositResponse.OptionResponse.builder()
                                .intrRateType(opt.getIntrRateType())
                                .intrRateTypeNm(opt.getIntrRateTypeNm())
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