package org.iebbuda.mozi.domain.product.mapper;

import org.iebbuda.mozi.domain.product.domain.SavingProduct;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SavingMapper {

    //전체 적금 조회
    List<SavingProduct>findAll();

    //단건 적금 조회
    SavingProduct findById(Long savingId);
}
