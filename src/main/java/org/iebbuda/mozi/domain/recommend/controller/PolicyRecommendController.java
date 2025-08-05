package org.iebbuda.mozi.domain.recommend.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.domain.recommend.dto.GoalPolicyRecommendDTO;
import org.iebbuda.mozi.domain.recommend.dto.PolicyRecommendDTO;
import org.iebbuda.mozi.domain.recommend.service.PolicyRecommendService;
import org.iebbuda.mozi.domain.security.account.domain.CustomUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend/policy")
public class PolicyRecommendController {

    private final PolicyRecommendService policyRecommendService;

    // 전체 목표 추천 (goalId 여러 개일 경우)
    @GetMapping("/all")
    public BaseResponse<List<GoalPolicyRecommendDTO>> recommendAll(
            @AuthenticationPrincipal CustomUser user) {

        int userId = user.getUser().getUserId();
        List<GoalPolicyRecommendDTO> result = policyRecommendService.recommendAll(userId);
        return new BaseResponse<>(result);
    }

    // 목표 별 상세 추천
    @GetMapping("/{goalId}")
    public BaseResponse<List<PolicyRecommendDTO>> recommendPolicies(
            @AuthenticationPrincipal CustomUser user,
            @PathVariable int goalId) {

        int userId = user.getUser().getUserId();
        List<PolicyRecommendDTO> result = policyRecommendService.recommend(userId, goalId);
        return new BaseResponse<>(result);
    }
    
    // 키워드 기반 추천
    @GetMapping("/keyword/{keyword}")
    public BaseResponse<List<PolicyRecommendDTO>> recommendPoliciesByKeyword(
            @AuthenticationPrincipal CustomUser user,
            @PathVariable String keyword) {

        int userId = user.getUser().getUserId();
        List<PolicyRecommendDTO> result = policyRecommendService.recommendByKeyword(userId, keyword);
        return new BaseResponse<>(result);
    }
}