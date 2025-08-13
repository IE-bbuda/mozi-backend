package org.iebbuda.mozi.domain.recommend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.account.dto.AccountResponseDTO;
import org.iebbuda.mozi.domain.account.service.AccountServiceImpl;
import org.iebbuda.mozi.domain.goal.domain.GoalVO;
import org.iebbuda.mozi.domain.goal.dto.GoalDTO;
import org.iebbuda.mozi.domain.goal.service.GoalServiceImpl;
import org.iebbuda.mozi.domain.recommend.dto.FinancialRecommendProductDTO;
import org.iebbuda.mozi.domain.recommend.dto.GoalRecommendationDTO;
import org.iebbuda.mozi.domain.recommend.mapper.FinancialRecommendMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            // 파생값 계산
            double achievementRate = calculateAchievementRate(goal.getGoalId(), userId);
            BigDecimal targetAmount = goal.getTargetAmount();

            long monthsLeft = ChronoUnit.MONTHS.between(LocalDate.now(), goal.getGoalDate().toLocalDate());
            monthsLeft = Math.max(1, monthsLeft); // 최소 1개월 보정

            BigDecimal monthlyNeed = targetAmount.subtract(getTotalBalance(goal.getGoalId(), userId))
                    .divide(BigDecimal.valueOf(monthsLeft), RoundingMode.CEILING);

            log.info("목표 ID: {}, 달성률: {}, 남은 개월 수: {}, 월 납입 필요액: {}",
                    goal.getGoalId(), achievementRate, monthsLeft, monthlyNeed);

            // 스코어링(예금/적금)
            int depositScore = 0;
            int savingsScore = 0;

            // 달성률 기반 점수
            if (achievementRate < 30) {
                savingsScore += 30;
            } else if (achievementRate > 70) {
                depositScore += 30;
            } else {
                depositScore += 15;
                savingsScore += 15;
            }

            // ✅ 목표 금액 기반 추가 보정
            if (targetAmount.compareTo(new BigDecimal("1000000")) <= 0) {
                savingsScore += 10;
                log.info("[targetAmount adjust] 소액 목표 → 적금 점수 +10");
            }

            // 키워드 기반 세분화(+ 적금/예금 옵션)
            GoalVO.GoalKeyword keyword = goal.getKeyword();
            String rsrvType = null;             // 적금 전용: 정액(S) / 자유(F)
            String intrRateTypeSavings = null;  // 적금: 단리(S) / 복리(M)
            String intrRateTypeDeposit = null;  // 예금: 단리(S) / 복리(M)

            if (keyword != null) {
                switch (keyword) {
                    case MARRIAGE, HOME_PURCHASE -> {
                        depositScore += 20;
                        rsrvType = "S";
                        String preferred = (monthsLeft >= 24) ? "M" : "S";
                        intrRateTypeSavings = preferred;
                        intrRateTypeDeposit = preferred;
                        log.info("[keyword-adjust] {} -> preferred intrRateType = {}", keyword, preferred);
                    }
                    case TRAVEL, HOBBY -> {
                        savingsScore += 20;
                        intrRateTypeSavings = "M";
                        intrRateTypeDeposit = "M";
                    }
                    case EMPLOYMENT, EDUCATION_FUND -> {
                        savingsScore += 20;
                        rsrvType = "F";
                        intrRateTypeSavings = "M";
                    }
                }
            }

            // 월 납입 필요액 기반 보정
            if (monthlyNeed.compareTo(new BigDecimal("1000000")) > 0) {
                depositScore += 15;
            } else {
                savingsScore += 10;
            }

            // 비율 기반 개수 계산 (총 4개 보장)
            int totalScore = Math.max(1, depositScore + savingsScore);
            int savingsCount = (int) Math.round(4 * ((double) savingsScore / totalScore));
            int depositCount = 4 - savingsCount;
            log.info("추천 수량 - 예금: {}, 적금: {} (depositScore={}, savingsScore={})",
                    depositCount, savingsCount, depositScore, savingsScore);

            // 추천 조회 + Fallback
            List<FinancialRecommendProductDTO> recommendedProducts = new ArrayList<>();

            if (savingsCount > 0) {
                List<FinancialRecommendProductDTO> savings =
                        financialrecommendMapper.findTopSavingsByOption(monthsLeft, savingsCount, rsrvType, intrRateTypeSavings);

                if (savings.size() < savingsCount) {
                    int remain = savingsCount - savings.size();
                    List<FinancialRecommendProductDTO> fallback =
                            financialrecommendMapper.findTopSavingsProducts(monthsLeft, remain);
                    savings.addAll(fallback);
                    log.info("[fallback:savings] 부족 {}개 일반 적금으로 보완", remain);
                }
                recommendedProducts.addAll(savings);
            }

            if (depositCount > 0) {
                if (intrRateTypeDeposit != null) {
                    List<FinancialRecommendProductDTO> deposits =
                            financialrecommendMapper.findTopDepositByOption(monthsLeft, depositCount, intrRateTypeDeposit);

                    if (deposits.size() < depositCount) {
                        int remain = depositCount - deposits.size();
                        List<FinancialRecommendProductDTO> fallback =
                                financialrecommendMapper.findTopDepositProducts(monthsLeft, remain);
                        deposits.addAll(fallback);
                        log.info("[fallback:deposit] 부족 {}개 일반 예금으로 보완", remain);
                    }
                    recommendedProducts.addAll(deposits);
                } else {
                    recommendedProducts.addAll(
                            financialrecommendMapper.findTopDepositProducts(monthsLeft, depositCount)
                    );
                }
            }

            goalRecommendations.add(new GoalRecommendationDTO(
                    goal.getGoalId(),
                    goal.getGoalName(),
                    recommendedProducts
            ));
        }

        return goalRecommendations;
    }

    private double calculateAchievementRate(int goalId, int userId) {
        BigDecimal totalBalance = getTotalBalance(goalId, userId);
        BigDecimal targetAmount = goalService.getGoal(goalId).getTargetAmount();
        double rate = goalService.calculateAchievementRate(totalBalance, targetAmount);
        log.info("총 잔액: {}, 목표 금액: {}, 계산된 달성률: {}", totalBalance, targetAmount, rate);
        return rate;
    }

    private BigDecimal getTotalBalance(int goalId, int userId) {
        @SuppressWarnings("unchecked")
        List<AccountResponseDTO> accounts = (List<AccountResponseDTO>) accountService
                .getAccountsByGoal(goalId, userId)
                .get("accountList");

        return accounts.stream()
                .map(a -> BigDecimal.valueOf(a.getBalance()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
