package org.iebbuda.mozi.domain.scrap.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.product.dto.SavingResponse;
import org.iebbuda.mozi.domain.scrap.domain.DepositScrap;
import org.iebbuda.mozi.domain.scrap.domain.SavingScrap;

import java.util.List;

@Mapper
public interface SavingScrapMapper {
    // 특정 유저의 예금 스크랩 목록 조회 (옵션까지 조인)
    List<SavingScrap> getUserScraps(@Param("userId") Long userId);

    // 스크랩 추가
    void addScrap(@Param("userId") Long userId, @Param("savingId") Long savingId);

    // 스크랩 삭제
    void removeScrap(@Param("userId") Long userId, @Param("savingId") Long savingId);

    /**
     * 사용자별 적금 스크랩 개수 조회
     */
    int countByUserId(@Param("userId") int userId);

    /**
     * 사용자의 모든 적금 스크랩 삭제 (회원탈퇴용)
     */
    int deleteByUserId(@Param("userId") int userId);
}
