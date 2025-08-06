package org.iebbuda.mozi.domain.recommend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.account.dto.AccountResponseDTO;
import org.iebbuda.mozi.domain.account.service.AccountServiceImpl;
import org.iebbuda.mozi.domain.goal.service.GoalServiceImpl;
import org.iebbuda.mozi.domain.recommend.dto.GoalRecommendationDTO;
import org.iebbuda.mozi.domain.recommend.dto.FinancialRecommendProductDTO;
import org.iebbuda.mozi.domain.recommend.mapper.FinancialRecommendMapper;
import org.iebbuda.mozi.domain.goal.mapper.GoalMapper; // 목표 정보 가져오는 Mapper
import org.iebbuda.mozi.domain.goal.dto.GoalDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecommendServiceImpl implements RecommendService {

    private final FinancialRecommendMapper financialrecommendMapper;
    private final GoalServiceImpl goalService;
    private final AccountServiceImpl accountService;

    @Override
    public List<GoalRecommendationDTO> getRecommendationsByUser(int userId) {
        List<GoalDTO> goals = goalService.getGoalListByUserId(userId);
        List<GoalRecommendationDTO> goalRecommendations = new ArrayList<>();

        for (GoalDTO goal : goals) {
            double achievementRate = calculateAchievementRate(goal.getGoalId(), userId);
            //목표일-현재일보다 짧거나 같은 상품 계산시 사용 ex. 6이면 1~6개월까지의 저축기간 상품옵션중 탐색
            long monthsLeft = ChronoUnit.MONTHS.between(LocalDate.now(), goal.getGoalDate().toLocalDate());//목표일-현재일 계산

            log.info("계산된 달 차이: "+monthsLeft);
            List<FinancialRecommendProductDTO> recommendedProducts = new ArrayList<>();
            goal.getKeyword();
            if (achievementRate < 30) {
                recommendedProducts.addAll(financialrecommendMapper.findTopSavingsProducts(monthsLeft, 4));
            } else if (achievementRate < 70) {
                recommendedProducts.addAll(financialrecommendMapper.findTopSavingsProducts(monthsLeft, 2));
                recommendedProducts.addAll(financialrecommendMapper.findTopDepositProducts(monthsLeft, 2));
            } else {
                recommendedProducts.addAll(financialrecommendMapper.findTopDepositProducts(monthsLeft, 4));
            }

            goalRecommendations.add(new GoalRecommendationDTO(goal.getGoalId(), goal.getGoalName(), recommendedProducts));
        }

        return goalRecommendations;
    }


    private double calculateAchievementRate(int goalId, int userId) {
        log.info("실제 계좌 잔액: " + accountService.getAccountsByGoal(goalId, userId));

        List<AccountResponseDTO> accounts = (List<AccountResponseDTO>) accountService
                .getAccountsByGoal(goalId, Math.toIntExact(userId))
                .get("accountList");

        BigDecimal totalBalance = accounts.stream()
                .map(a -> BigDecimal.valueOf(a.getBalance()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("계좌 합산 금액: "+totalBalance);
        log.info("달성률: "+goalService.calculateAchievementRate(totalBalance,goalService.getGoal(goalId).getTargetAmount()));
        return goalService.calculateAchievementRate(
                totalBalance,
                goalService.getGoal(goalId).getTargetAmount()
        );
    }
}