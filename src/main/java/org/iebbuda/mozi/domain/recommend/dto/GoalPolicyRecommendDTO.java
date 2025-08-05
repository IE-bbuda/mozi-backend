package org.iebbuda.mozi.domain.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalPolicyRecommendDTO {
    // 목표 ID
    private int goalId;
    // 추천 키워드 (enum name, 예: TRAVEL, EMPLOYMENT)
    private String keyword;
    // 목표 이름 (예: 1억 모으기, 내집마련)
    private String goalName;
    // 추천된 정책 목록
    private List<PolicyRecommendDTO> recommendations;
}
