package org.iebbuda.mozi.domain.product.mapper;

import org.iebbuda.mozi.domain.product.domain.DepositProduct;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface DepositMapper {
    List<DepositProduct> findAllWithOptions();
    DepositProduct findByIdWithOptions(Long depositId);
}
