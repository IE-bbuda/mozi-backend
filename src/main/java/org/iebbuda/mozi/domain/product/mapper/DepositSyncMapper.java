package org.iebbuda.mozi.domain.product.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.iebbuda.mozi.domain.product.domain.DepositOption;
import org.iebbuda.mozi.domain.product.domain.DepositProduct;

@Mapper
public interface DepositSyncMapper {

    /**
     * 금융상품 코드로 기존 정기예금 상품 조회
     */
    DepositProduct findByProductCode(String finPrdtCd);

    /**
     * 정기예금 상품 저장
     */
    void insertProduct(DepositProduct product);

    /**
     * 기존 정기예금 상품 업데이트
     */
    void updateProduct(DepositProduct product);

    /**
     * 특정 상품에 연결된 옵션 삭제
     */
    void deleteOptionsByProductId(Long depositId);

    /**
     * 정기예금 옵션 저장
     */
    void insertOption(DepositOption option);
}