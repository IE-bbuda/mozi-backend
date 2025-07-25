package org.iebbuda.mozi.product.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.iebbuda.mozi.product.domain.DepositOption;

import java.util.List;

@Mapper
public interface DepositOptionMapper {

    /**
     * 특정 정기예금에 대한 옵션 리스트 조회
     */
    List<DepositOption> findByProductId(Long depositId);
}
