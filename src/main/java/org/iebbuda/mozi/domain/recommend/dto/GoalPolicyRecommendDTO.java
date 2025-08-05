package org.iebbuda.mozi.domain.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class GoalPolicyRecommendDTO {
    private int goalId;
    private String keyword;
    private List<PolicyRecommendDTO> recommendations;
}
