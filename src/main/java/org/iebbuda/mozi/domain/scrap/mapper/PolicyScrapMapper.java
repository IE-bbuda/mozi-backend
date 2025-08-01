package org.iebbuda.mozi.domain.scrap.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.scrap.domain.PolicyScrapVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PolicyScrapMapper {

    // 스크랩 추가
    void insertScrap(PolicyScrapVO vo);

    // 스크랩 취소
    void deleteScrap(@Param("userId") int userId, @Param("policyId") int policyId);

    // 스크랩 여부 조회
    boolean existsScrap(@Param("userId") int userId, @Param("policyId") int policyId);

    // 스크랩한 정책 ID 목록 조회
    List<Integer> getScrapPolicyIds(@Param("userId") int userId);

    List<PolicyVO> getScrapedPolicies(int userId);

}
