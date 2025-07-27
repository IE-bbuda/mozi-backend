package org.iebbuda.mozi.domain.product.mapper;

import org.iebbuda.mozi.domain.product.domain.DepositProduct;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface DepositMapper {

    //전체 정기예금 조회
    List<DepositProduct> findAll();

    DepositProduct findById(Long depositId);


}
