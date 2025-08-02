package org.iebbuda.mozi.domain.scrap.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.scrap.domain.DepositScrap;

import java.util.List;

@Mapper
public interface DepositScrapMapper {
    // 특정 유저의 예금 스크랩 목록 조회 (옵션까지 조인)
    List<DepositScrap> getUserScraps(@Param("userId") Long userId);

    // 스크랩 추가
    void addScrap(@Param("userId") Long userId, @Param("depositId") Long depositId);

    // 스크랩 삭제
    void removeScrap(@Param("userId") Long userId, @Param("depositId") Long depositId);
}
