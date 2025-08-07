package org.iebbuda.mozi.domain.policy.service;

import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.policy.dto.PolicyDTO;
import org.iebbuda.mozi.domain.policy.dto.PolicyFilterDTO;

import java.util.List;

public interface PolicyService {

    // 전체 정책 목록 조회
    List<PolicyDTO> findAll();

    // 전체 정책 목록 조회
    void saveAll(List<PolicyDTO> dtoList);

    // 정책 저장 (중복 방지)
    PolicyDTO findById(int id);

    // 필터 조건으로 정책 리스트 조회
    List<PolicyDTO> getPoliciesByFilters(PolicyFilterDTO filters);

    // 마감 임박 정책 조회
    List<PolicyVO> getDeadlineSoonPolicies(int days);
}
