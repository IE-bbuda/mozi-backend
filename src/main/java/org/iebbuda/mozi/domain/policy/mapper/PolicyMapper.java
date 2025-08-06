package org.iebbuda.mozi.domain.policy.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.policy.dto.PolicyDTO;
import org.iebbuda.mozi.domain.policy.dto.PolicyFilterDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PolicyMapper {

    // 전체 정책 목록 조회
    List<PolicyVO> findAll();

    // 정책 고유번호로 중복 여부 확인
    int existsByPlcyNo(String plcyNo);

    // 정책 DB에 insert
    void insertPolicy(PolicyVO policyVO);

    // ID로 상세 조회
    PolicyVO selectPolicyById(int id);

    // 필터 조건으로 정책 리스트 조회
    List<PolicyDTO> findByFilters(PolicyFilterDTO filters);

    // 현재 DB에 저장된 정책 개수 반환
    int count();

    // 마감 임박 정책 조히
    List<PolicyVO> selectDeadlineSoonPolicies(@Param("days") int days);
}
