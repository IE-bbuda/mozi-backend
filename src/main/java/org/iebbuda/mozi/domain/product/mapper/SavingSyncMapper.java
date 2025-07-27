package org.iebbuda.mozi.domain.product.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.iebbuda.mozi.domain.product.domain.SavingOption;
import org.iebbuda.mozi.domain.product.domain.SavingProduct;

@Mapper
public interface SavingSyncMapper {

    /**
     * 금융상품 코드로 기존 적금 상품 조회
     */
    SavingProduct findByProductCode(String finPrdtCd);

    /**
     * 적금 상품 저장
     */
    void insertProduct(SavingProduct product);

    /**
     * 기존 적금 상품 업데이트
     */
    void updateProduct(SavingProduct product);

    /**
     * 특정 적금 상품에 연결된 옵션 삭제
     */
    void deleteOptionsByProductId(Long savingId);

    /**
     * 적금 옵션 저장
     */
    void insertOption(SavingOption option);
}

