package org.iebbuda.mozi.domain.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalRecommendationDTO {
    private int goalId;
    private String goalName;
    private List<FinancialRecommendProductDTO> recommendedProducts;
}
