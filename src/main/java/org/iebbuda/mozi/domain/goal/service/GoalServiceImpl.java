package org.iebbuda.mozi.domain.goal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.goal.dto.GoalDTO;
import org.iebbuda.mozi.goal.domain.GoalVO;
import org.iebbuda.mozi.goal.mapper.GoalMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class GoalServiceImpl implements GoalService {

    private final GoalMapper goalMapper;

    @Override
    public List<GoalDTO> getGoalList() {
        log.info("전체 목표 목록 조회");
        List<GoalVO> goalVOList = goalMapper.getList();
        return goalVOList.stream()
                .map(GoalDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<GoalDTO> getGoalListByUserId(int userId) {
        log.info("사용자별 목표 목록 조회 - 사용자 ID: {}", userId);
        List<GoalVO> userGoals = goalMapper.getListByUserId(userId);
        return userGoals.stream()
                .map(GoalDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    public GoalDTO getGoal(int goalId) {
        log.info("목표 조회 - 목표 ID: {}", goalId);
        GoalVO goalVO = goalMapper.get(goalId);
        return GoalDTO.of(goalVO);
    }

    @Override
    public GoalDTO createGoal(GoalDTO goalDTO) {
        log.info("목표 생성 - 목표명: {}", goalDTO.getGoalName());

        // 생성 시간은 DB에서 자동 설정 (now() 함수 사용)
        // 목표 상태 초기화 (미달성) - DB 기본값이 false이므로 설정하지 않아도 됨
        if (goalDTO.isGoalStatus() == false) {
            goalDTO.setGoalStatus(false);
        }

        GoalVO goalVO = goalDTO.toVo();
        goalMapper.create(goalVO);

        // 생성된 목표 ID를 DTO에 설정
        goalDTO.setGoalId(goalVO.getGoalId());

        return goalDTO;
    }

    @Override
    public boolean updateGoal(GoalDTO goalDTO) {
        log.info("목표 수정 - 목표 ID: {}", goalDTO.getGoalId());

        // 수정 시간은 DB에서 자동 설정 (now() 함수 사용)
        GoalVO goalVO = goalDTO.toVo();
        int result = goalMapper.update(goalVO);

        return result > 0;
    }

    @Override
    public boolean deleteGoal(int goalId) {
        log.info("목표 삭제 - 목표 ID: {}", goalId);
        int result = goalMapper.delete(goalId);
        return result > 0;
    }

    @Override
    public double calculateAchievementRate(BigDecimal currentAmount, BigDecimal targetAmount) {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        if (currentAmount == null) {
            currentAmount = BigDecimal.ZERO;
        }

        // 달성률 계산 (백분율)
        BigDecimal rate = currentAmount.divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        // 100%를 초과하지 않도록 제한
        if (rate.compareTo(BigDecimal.valueOf(100)) > 0) {
            return 100.0;
        }

        return rate.doubleValue();
    }

    @Override
    public LocalDateTime calculateExpectedAchievementDate(int goalId, BigDecimal monthlyAmount) {
        GoalVO goal = goalMapper.get(goalId);
        if (goal == null || monthlyAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        // 현재 금액 (실제로는 계좌 연동 API에서 가져와야 함)
        BigDecimal currentAmount = BigDecimal.ZERO; // 추후 계좌 연동 API 연결
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentAmount);

        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return LocalDateTime.now(); // 이미 달성
        }

        // 필요한 개월 수 계산
        BigDecimal monthsNeeded = remainingAmount.divide(monthlyAmount, 0, RoundingMode.UP);

        return LocalDateTime.now().plusMonths(monthsNeeded.longValue());
    }

    @Override
    public boolean isGoalAchieved(int goalId) {
        GoalVO goal = goalMapper.get(goalId);
        if (goal == null) {
            return false;
        }

        // 실제로는 계좌 연동 API에서 현재 금액을 가져와야 함
        BigDecimal currentAmount = BigDecimal.ZERO; // 추후 계좌 연동 API 연결

        return currentAmount.compareTo(goal.getTargetAmount()) >= 0;
    }

    @Override
    public boolean updateGoalStatus(int goalId, boolean status) {
        log.info("목표 상태 업데이트 - 목표 ID: {}, 상태: {}", goalId, status);

        GoalVO goal = goalMapper.get(goalId);
        if (goal == null) {
            return false;
        }

        goal.setGoalStatus(status);
        // 수정 시간은 DB에서 자동 설정 (now() 함수 사용)

        int result = goalMapper.update(goal);
        return result > 0;
    }

    @Override
    public GoalDTO getBillionGoal(int userId) {
        log.info("1억 모으기 목표 조회 - 사용자 ID: {}", userId);
        GoalVO billionGoal = goalMapper.getBillionGoal(userId);
        return GoalDTO.of(billionGoal);
    }


    @Override
    public BigDecimal calculateRemainingAmount(int goalId) {
        GoalVO goal = goalMapper.get(goalId);
        if (goal == null) {
            return BigDecimal.ZERO;
        }

        // 현재 금액 (실제로는 계좌 연동 API에서 가져와야 함)
        BigDecimal currentAmount = BigDecimal.ZERO; // 추후 계좌 연동 API 연결

        BigDecimal remaining = goal.getTargetAmount().subtract(currentAmount);
        return remaining.compareTo(BigDecimal.ZERO) > 0 ? remaining : BigDecimal.ZERO;
    }

    @Override
    public long calculateRemainingDays(int goalId) {
        GoalVO goal = goalMapper.get(goalId);
        if (goal == null) {
            return 0;
        }

        long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), goal.getGoalDate());
        return remainingDays > 0 ? remainingDays : 0;
    }
    @Override
    public GoalVO.GoalKeyword getGoalKeyword(int goalId) {
        log.info("목표 키워드 조회 - 목표 ID: {}", goalId);
        GoalVO goal = goalMapper.get(goalId);
        if (goal == null) {
            log.warn("목표를 찾을 수 없습니다 - 목표 ID: {}", goalId);
            return null;
        }
        return goal.getKeyword();
    }
}