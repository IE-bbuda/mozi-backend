package org.iebbuda.mozi.policy.mapper;

import org.iebbuda.mozi.policy.domain.PolicyVO;
import org.iebbuda.mozi.policy.dto.PolicyDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PolicyMapper {
    List<PolicyVO> findAll();
    int existsByPlcyNo(String plcyNo);
    void insertPolicy(PolicyVO policyVO);

    PolicyVO selectPolicyById(int id);

}
