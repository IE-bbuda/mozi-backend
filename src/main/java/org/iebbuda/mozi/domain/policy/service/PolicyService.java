package org.iebbuda.mozi.domain.policy.service;

import org.iebbuda.mozi.domain.policy.dto.PolicyDTO;
import org.iebbuda.mozi.domain.policy.dto.PolicyFilterDTO;

import java.util.List;

public interface PolicyService {

    List<PolicyDTO> findAll();

    void saveAll(List<PolicyDTO> dtoList);

    PolicyDTO findById(int id);

    List<PolicyDTO> getPoliciesByFilters(PolicyFilterDTO filters);

}
