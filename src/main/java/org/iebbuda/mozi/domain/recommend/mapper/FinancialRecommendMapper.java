package org.iebbuda.mozi.domain.recommend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.recommend.dto.FinancialRecommendProductDTO;


import java.util.List;

@Mapper
public interface FinancialRecommendMapper {

    // 기존
    List<FinancialRecommendProductDTO> findTopDepositProducts(@Param("monthsLeft") long monthsLeft,
                                                              @Param("limit") int limit);

    List<FinancialRecommendProductDTO> findTopSavingsProducts(@Param("monthsLeft") long monthsLeft,
                                                              @Param("limit") int limit);

    // 신규: 예금 옵션 필터
    List<FinancialRecommendProductDTO> findTopDepositByOption(@Param("monthsLeft") long monthsLeft,
                                                              @Param("limit") int limit,
                                                              @Param("intrRateType") String intrRateType);

    // 신규: 적금 옵션 필터
    List<FinancialRecommendProductDTO> findTopSavingsByOption(@Param("monthsLeft") long monthsLeft,
                                                              @Param("limit") int limit,
                                                              @Param("rsrvType") String rsrvType,
                                                              @Param("intrRateType") String intrRateType);

    // rsrv_type만 조건으로 하는 적금 조회
    List<FinancialRecommendProductDTO> findTopSavingsByRsrvType(@Param("monthsLeft") long monthsLeft,
                                                                @Param("limit") int limit,
                                                                @Param("rsrvType") String rsrvType);
}