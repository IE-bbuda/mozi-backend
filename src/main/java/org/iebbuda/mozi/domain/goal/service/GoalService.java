package org.iebbuda.mozi.domain.goal.service;

import org.iebbuda.mozi.domain.goal.dto.GoalDTO;
import org.iebbuda.mozi.domain.goal.domain.GoalVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface GoalService {

    /*목표 전체 목록 조회*/
    List<GoalDTO> getGoalList();

    /*사용자별 목표 목록 조회*/
    List<GoalDTO> getGoalListByUserId(int userId);

    /* 특정 목표 조회*/
    GoalDTO getGoal(int goalId);

    /* 목표 생성*/
    GoalDTO createGoal(GoalDTO goalDTO);

    /* 목표 수정*/
    boolean updateGoal(GoalDTO goalDTO);

    /* 목표 삭제*/
    boolean deleteGoal(int goalId);

    /*
     * 목표 달성률 계산
     * currentAmount= 현재 금액(나중에 계좌 api를 이용)
     * targetAmount= 목표 금액
     * return 달성률 (0-100%)
     */
    double calculateAchievementRate(BigDecimal currentAmount, BigDecimal targetAmount);

    /*목표 달성 예상 날짜 계산 @param monthlyAmount 월 적립 금액*/
    LocalDateTime calculateExpectedAchievementDate(int goalId, BigDecimal monthlyAmount);

    /*목표 달성 여부 확인*/
    boolean isGoalAchieved(int goalId);

    /* 목표 상태 업데이트 (달성/미달성)*/
    boolean updateGoalStatus(int goalId, boolean status);

    /* 사용자의 1억 모으기 목표 조회*/
    GoalDTO getBillionGoal(int userId);

    /* 목표까지 남은 금액 계산*/
    BigDecimal calculateRemainingAmount(int goalId);

    /*
     * 목표까지 남은 기간 계산 (일 단위)
     * @return 남은 일수
     */
    long calculateRemainingDays(int goalId);


    /* 목표 ID로 키워드 조회 추천 알고리즘 용
     return 목표 키워드 (목표가 없으면 null)*/
    GoalVO.GoalKeyword getGoalKeyword(int goalId);


    GoalVO getGoalById(int goalId); // goalId로 GoalVO 가져오기
    List<GoalVO> getGoalVOListByUserId(int userId);

    //유저 제거
    int getGoalCountByUserId(int userId);
    void deleteAllGoalsByUserId(int userId);
}