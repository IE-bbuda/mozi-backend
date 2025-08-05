package org.iebbuda.mozi.domain.product.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.product.domain.SavingProduct;
import org.iebbuda.mozi.domain.product.dto.SavingResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SavingMapper {

    //전체 적금 조회
    List<SavingProduct>findAllWithOptions();
    //단건 적금 조회
    SavingProduct findByIdWithOptions(Long savingId);

    List<SavingProduct>findTopSavingProduct(@Param("limit")int limit);
}
