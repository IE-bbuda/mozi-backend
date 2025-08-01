package org.iebbuda.mozi.domain.policy.controller;

import lombok.RequiredArgsConstructor;
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


//    // 정책 API에서 받아와 DB에 저장(수동 버전.)
//    @PostMapping(value = "/fetch-and-save", produces = "text/plain; charset=UTF-8")
//    public ResponseEntity<String> fetchAndSave() {
//        String json = apiCaller.getJsonResponse();
//        List<PolicyDTO> dtoList = apiCaller.parseJsonToPolicies(json);
//        policyService.saveAll(dtoList);
//
//        return ResponseEntity.ok("정책 정보 DB 저장 완료됨!!");
//    }
}
