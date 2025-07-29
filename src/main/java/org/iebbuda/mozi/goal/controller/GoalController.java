package org.iebbuda.mozi.goal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.goal.dto.GoalDTO;
import org.iebbuda.mozi.goal.service.GoalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
@Log4j2
public class GoalController {

    final private GoalService service;

    /* 전체 조회 */
//    @GetMapping("")
//    public List<GoalDTO> getGoalList(){
//        return service.getGoalList();
//    }

    /* 특정 사용자의 모든 목표 조회 */
    @GetMapping("{userId}")
    public List<GoalDTO> getGoalListByUserId(@PathVariable int userId){
        log.info("사용자별 목표 조회 - userId: {}", userId);
        return service.getGoalListByUserId(userId);
    }

    /* 특정 사용자의 특정 목표 조회 */
    @GetMapping("/{userId}/{goalId}")
    public GoalDTO getGoal(@PathVariable int userId, @PathVariable int goalId){
        log.info("특정 사용자의 특정 목표 조회 - userId : {}, goalId : {}", userId, goalId);
        GoalDTO goal = service.getGoal(goalId);

        return goal;
    }

    /* 특정 사용자의 새 목표 생성 */
    @PostMapping("/{userId}")
    public GoalDTO createGoal(@PathVariable int userId, @RequestBody GoalDTO goalDTO){
        log.info("목표 생성 요청 - userId: {}, goalName: {}", userId, goalDTO);
        // userId가 DTO에 없으면 세팅해줌
        if (goalDTO.getUserId() != userId) {
            goalDTO.setUserId(userId);
        }

        return service.createGoal(goalDTO);
    }

    /* 특정 사용자의 특정 목표 수정*/
    @PutMapping("/{userId}/{goalId}")
    public boolean updateGoal(@PathVariable int userId, @PathVariable int goalId, @RequestBody GoalDTO goalDTO){
        log.info("목표 수정 요청 - userId: {}, goalId : {}", userId, goalId);

        goalDTO.setUserId(userId);
        goalDTO.setGoalId(goalId);

        return service.updateGoal(goalDTO);
    }

    /* 특정 사용자의 특정 목표 삭제 */
    @DeleteMapping("/{userId}/{goalId}")
    public boolean deleteGoal(@PathVariable int userId, @PathVariable int goalId){
        log.info("목표 삭제 요청 - userId: {}, goalId: {}", userId, goalId);
        GoalDTO goal = service.getGoal(goalId);

        return service.deleteGoal(goalId);
    }



    /* 목표 달성률 계산 */
    /* GET /goal/{userId}/{goalId}/achievement-rate */
    @GetMapping("/{userId}/{goalId}/achievement-rate")
    public double getAchievementRate(@PathVariable int userId, @PathVariable int goalId){
        log.info("목표 달성률 조회 - userId: {}, goalId : {}", userId, goalId);
        // 현재 금액은 0으로 가정 (실제 계좌 연동 api 필요)
        return service.calculateAchievementRate(
                BigDecimal.ZERO,
                service.getGoal(goalId).getTargetAmount()
        );
    }

    /* 목표 달성 예상 날짜 */
    /* GET /goal/{userId}/{goalId}/expected-date */
    @GetMapping("/{userId}/{goalId}/expected-date")
    public String getExpectedAchievementDate(
            @PathVariable int userId, @PathVariable int goalId,
            @RequestParam("monthlyAmount") BigDecimal monthlyAmount)
    {
        log.info("목표 달성 예상 날짜 조회 - userId : {}, goalId : {}, monthlyAmount: {}", userId, goalId, monthlyAmount);
        return String.valueOf(service.calculateExpectedAchievementDate(goalId, monthlyAmount));
    }

    /* 목표 달성 여부 확인 */
    /* GET /goal/{userId}/{goalId}/is-achieved */
    @GetMapping("/{userId}/{goalId}/is-achieved")
    public boolean isGoalAchieved(@PathVariable int userId, @PathVariable int goalId){
        log.info("목표 달성 여부 확인 - userId: {}, goalId : {}", userId, goalId);
        return service.isGoalAchieved(goalId);
    }

    /* 목표 상태 업데이트 */
    /* PATCH /goal/{userId}/{goalId}/status */
    @PatchMapping("/{userId}/{goalId}/status")
    public boolean updateGoalStatus(
            @PathVariable int userId, @PathVariable int goalId,
            @RequestBody boolean status)
    {
        log.info("목표 상태 업데이트 - userId: {}, goalId : {}, status: {}", userId, goalId, status);
        return service.updateGoalStatus(goalId, status);
    }

    /* 1억 모으기 목표 조회 */
    /*  GET /goal/{userId}/billion */
    @GetMapping("/{userId}/billion")
    public GoalDTO getBillionGoal(@PathVariable int userId){
        log.info("1억 모으기 목표 조회 - userId : {}", userId);
        return service.getBillionGoal(userId);
    }

    /* 남은 금액 계산 */
    /* GET /goal/{userId}/{goalId}/remaining-amount */
    @GetMapping("/{userId}/{goalId}/remaining-amount")
    public BigDecimal getRemainingAmount(@PathVariable int userId, @PathVariable int goalId){
        log.info("목표 남은 금액 계산 - userId : {}, goalId : {}", userId, goalId);
        return service.calculateRemainingAmount(goalId);
    }

    /* 남은 기간 계산 */
    /* GET /goal/{userId}/{goalId}/remaining-days */
    @GetMapping("/{userId}/{goalId}/remaining-days")
    public long getRemainingDays(@PathVariable int userId, @PathVariable int goalId){
        log.info("목표 남은 기간 계산 - userId : {}, goalId : {}", userId, goalId);
        return service.calculateRemainingDays(goalId);
    }

    /* 목표 키워드 조회 */
    /* GET /goal/{userId}/{goalId}/keyword */
    @GetMapping("/{userId}/{goalId}/keyword")
    public String getGoalKeyword(@PathVariable int userId, @PathVariable int goalId){
        log.info("목표 키워드 조회 - userId : {}, goalId : {}", userId, goalId);
        return String.valueOf(service.getGoalKeyword(goalId));
    }


}
