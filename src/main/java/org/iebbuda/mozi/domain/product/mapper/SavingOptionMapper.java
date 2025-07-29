package org.iebbuda.mozi.domain.product.mapper;

import org.iebbuda.mozi.domain.product.domain.SavingOption;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SavingOptionMapper {
    /**
     * 특정 적금에 대한 옵션 리스트 조회
     */
    List<SavingOption> findByProductId(Long savingId);
}
