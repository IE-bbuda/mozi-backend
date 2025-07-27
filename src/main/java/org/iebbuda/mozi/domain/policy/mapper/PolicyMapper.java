package org.iebbuda.mozi.domain.policy.mapper;

import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PolicyMapper {
    List<PolicyVO> findAll();
    int existsByPlcyNo(String plcyNo);
    void insertPolicy(PolicyVO policyVO);

    PolicyVO selectPolicyById(int id);

}
