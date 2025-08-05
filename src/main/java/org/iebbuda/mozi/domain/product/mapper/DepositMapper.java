package org.iebbuda.mozi.domain.product.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.product.domain.DepositProduct;
import org.iebbuda.mozi.domain.product.domain.SavingProduct;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface DepositMapper {
    List<DepositProduct> findAllWithOptions();
    DepositProduct findByIdWithOptions(Long depositId);
    List<DepositProduct>findTopDepositProduct(@Param("limit")int limit);
}
