package org.iebbuda.mozi.domain.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecommendProductDTO {
    private int productId;
    private String productName;     // 상품명
    private String bankCode;
    private String bankName;        // 은행명
    private double intRate;         // 기본 금리
    private double intRate2;        // 우대 금리
    private int saveTrm;            // 저축 기간 (개월)
    private String productType;     // 예금(DEPOSIT) or 적금(SAVINGS)
}
