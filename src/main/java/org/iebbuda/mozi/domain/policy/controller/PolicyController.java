package org.iebbuda.mozi.domain.policy.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.domain.policy.domain.PolicyVO;
import org.iebbuda.mozi.domain.policy.dto.PolicyDTO;
import org.iebbuda.mozi.domain.policy.dto.PolicyFilterDTO;
import org.iebbuda.mozi.domain.policy.service.PolicyService;
import org.iebbuda.mozi.domain.policy.util.ApiCaller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;
    private final ApiCaller apiCaller;

    // 정책 전체 조회
    @GetMapping
    public ResponseEntity<List<PolicyDTO>> getAllPolicies() {

        List<PolicyDTO> list = policyService.findAll();
        return ResponseEntity.ok(list);
    }

    // 정책 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PolicyDTO> getPolicyById(@PathVariable("id") int id) {
        PolicyDTO dto = policyService.findById(id);
        return ResponseEntity.ok(dto);
    }


    // 필터 조건에 따른 정책 조회
    @PostMapping("/filter")
    public List<PolicyDTO> getFilteredPolicies(@RequestBody PolicyFilterDTO filters) {
        return policyService.getPoliciesByFilters(filters);
    }

    // 마감 임박 정책 조회
    @GetMapping("/deadline")
    public BaseResponse<List<PolicyVO>> getDeadlineSoonPolicies(@RequestParam(defaultValue = "31") int days) {
        List<PolicyVO> result = policyService.getDeadlineSoonPolicies(days);
        return new BaseResponse<>(result);
    }
}
