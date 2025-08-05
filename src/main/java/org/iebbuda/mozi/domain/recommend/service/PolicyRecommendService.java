package org.iebbuda.mozi.domain.recommend.service;

import org.iebbuda.mozi.domain.goal.domain.GoalVO;
import org.iebbuda.mozi.domain.goal.service.GoalService;
import org.iebbuda.mozi.domain.recommend.dto.GoalPolicyRecommendDTO;
import org.iebbuda.mozi.domain.recommend.dto.PolicyRecommendDTO;

import java.util.List;

public interface PolicyRecommendService {


    // goal id 별 추천
    List<PolicyRecommendDTO> recommend(int userId, int goalId);

    // 키워드 별 추천
    List<PolicyRecommendDTO> recommendByKeyword(int userId, String keyword); // ← 추가

    List<GoalPolicyRecommendDTO> recommendAll(int userId);
}
