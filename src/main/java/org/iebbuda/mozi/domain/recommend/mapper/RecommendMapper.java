package org.iebbuda.mozi.domain.recommend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.recommend.dto.RecommendProductDTO;

import java.util.List;

@Mapper
public interface RecommendMapper {

    List<RecommendProductDTO> findTopSavingsProducts(
            @Param("monthsLeft") long monthsLeft,
            @Param("limit") int limit

    );

    List<RecommendProductDTO> findTopDepositProducts(
            @Param("monthsLeft") long monthsLeft,
            @Param("limit") int limit
    );
}