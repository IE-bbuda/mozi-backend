package org.iebbuda.mozi.policy.service;

import org.iebbuda.mozi.policy.domain.PolicyVO;
import org.iebbuda.mozi.policy.dto.PolicyDTO;

import java.util.List;

public interface PolicyService {

    List<PolicyDTO> findAll();

    void saveAll(List<PolicyDTO> dtoList);

    PolicyDTO findById(int id);

}
