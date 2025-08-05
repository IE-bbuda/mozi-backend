package org.iebbuda.mozi.domain.recommend.service;

import org.iebbuda.mozi.domain.recommend.dto.GoalRecommendationDTO;


import java.util.List;

public interface RecommendService {
    List<GoalRecommendationDTO> getRecommendationsByUser(int userId);
}
