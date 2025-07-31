package org.iebbuda.mozi.domain.scrab.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.scrab.domain.PolicyScrabVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PolicyScrabMapper {

    // 스크랩 추가
    void insertScrab(PolicyScrabVO vo);

    // 스크랩 취소
    void deleteScrab(@Param("userId") int userId, @Param("policyId") int policyId);

    // 스크랩 여부 조회
    boolean existsScrab(@Param("userId") int userId, @Param("policyId") int policyId);

    // 스크랩한 정책 ID 목록 조회
    List<Integer> getScrabPolicyIds(int userId);

}
