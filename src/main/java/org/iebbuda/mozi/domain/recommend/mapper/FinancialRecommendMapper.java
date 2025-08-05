package org.iebbuda.mozi.domain.recommend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.recommend.dto.FinancialRecommendProductDTO;


import java.util.List;

@Mapper
public interface FinancialRecommendMapper {

    List<FinancialRecommendProductDTO> findTopSavingsProducts(
            @Param("monthsLeft") long monthsLeft,
            @Param("limit") int limit

    );

    List<FinancialRecommendProductDTO> findTopDepositProducts(
            @Param("monthsLeft") long monthsLeft,
            @Param("limit") int limit
    );
}