package org.iebbuda.mozi.domain.goal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.account.dto.AccountResponseDTO;
import org.iebbuda.mozi.domain.account.service.AccountService;
import org.iebbuda.mozi.domain.account.service.AccountServiceImpl;
import org.iebbuda.mozi.domain.goal.dto.GoalDTO;
import org.iebbuda.mozi.domain.goal.service.GoalService;
import org.iebbuda.mozi.domain.security.account.domain.CustomUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
@Log4j2
public class GoalController {

    final private GoalService service;
    final private AccountService accountService;

    /* 현재 사용자의 모든 목표 조회 */
    @GetMapping
    public List<GoalDTO> getGoals(@AuthenticationPrincipal CustomUser user){
        // 인증되지 않은 사용자 체크
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("사용자별 목표 조회 - userId: {}", userId);
        return service.getGoalListByUserId(userId);
    }

    /* 현재 사용자의 특정 목표 조회 */
    @GetMapping("/{goalId}")
    public GoalDTO getGoal(@PathVariable int goalId, @AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("특정 사용자의 특정 목표 조회 - userId : {}, goalId : {}", userId, goalId);
        GoalDTO goal = service.getGoal(goalId);
        return goal;
    }

    /* 현재 사용자의 새 목표 생성 */
    @PostMapping
    public GoalDTO createGoal(@RequestBody GoalDTO goalDTO, @AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 생성 요청 - userId: {}, goalName: {}", userId, goalDTO.getGoalName());

        // userId 세팅
        goalDTO.setUserId(userId);

        return service.createGoal(goalDTO);
    }

    /* 현재 사용자의 특정 목표 수정*/
    @PutMapping("/{goalId}")
    public boolean updateGoal(@PathVariable int goalId, @RequestBody GoalDTO goalDTO, @AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 수정 요청 - userId: {}, goalId : {}", userId, goalId);

        goalDTO.setUserId(userId);
        goalDTO.setGoalId(goalId);

        return service.updateGoal(goalDTO);
    }

    /* 현재 사용자의 특정 목표 삭제 */
    @DeleteMapping("/{goalId}")
    public boolean deleteGoal(@PathVariable int goalId, @AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 삭제 요청 - userId: {}, goalId: {}", userId, goalId);
        GoalDTO goal = service.getGoal(goalId);

        return service.deleteGoal(goalId);
    }

    /* 목표 달성률 계산 */
    @GetMapping("/{goalId}/achievement-rate")
    public double getAchievementRate(@PathVariable int goalId, @AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 달성률 조회 - userId: {}, goalId : {}", userId, goalId);
        // 현재 금액은 0으로 가정 (실제 계좌 연동 api 필요)


        //실제 계좌의 잔액
        log.info("실제 계좌 잔액:"+accountService.getAccountsByGoal(goalId,userId));
        List<AccountResponseDTO> accounts = (List<AccountResponseDTO>) accountService
                .getAccountsByGoal(goalId, userId)
                .get("accountList");

        BigDecimal totalBalance = accounts.stream()
                .map(a -> BigDecimal.valueOf(a.getBalance())) // Double → BigDecimal 변환
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return service.calculateAchievementRate(
                totalBalance,
                service.getGoal(goalId).getTargetAmount()
        );
    }

    /* 목표 달성 예상 날짜 */
    @GetMapping("/{goalId}/expected-date")
    public String getExpectedAchievementDate(
            @PathVariable int goalId,
            @RequestParam("monthlyAmount") BigDecimal monthlyAmount,
            @AuthenticationPrincipal CustomUser user)
    {
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 달성 예상 날짜 조회 - userId : {}, goalId : {}, monthlyAmount: {}", userId, goalId, monthlyAmount);
        return String.valueOf(service.calculateExpectedAchievementDate(goalId, monthlyAmount));
    }

    /* 목표 달성 여부 확인 */
    @GetMapping("/{goalId}/is-achieved")
    public boolean isGoalAchieved(@PathVariable int goalId, @AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 달성 여부 확인 - userId: {}, goalId : {}", userId, goalId);
        return service.isGoalAchieved(goalId);
    }

    /* 목표 상태 업데이트 */
    @PatchMapping("/{goalId}/status")
    public boolean updateGoalStatus(
            @PathVariable int goalId,
            @RequestBody boolean status,
            @AuthenticationPrincipal CustomUser user)
    {
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 상태 업데이트 - userId: {}, goalId : {}, status: {}", userId, goalId, status);
        return service.updateGoalStatus(goalId, status);
    }

    /* 1억 모으기 목표 조회 */
    @GetMapping("/billion")
    public GoalDTO getBillionGoal(@AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("1억 모으기 목표 조회 - userId : {}", userId);
        return service.getBillionGoal(userId);
    }

    /* 남은 금액 계산 */
    @GetMapping("/{goalId}/remaining-amount")
    public BigDecimal getRemainingAmount(@PathVariable int goalId, @AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 남은 금액 계산 - userId : {}, goalId : {}", userId, goalId);
        return service.calculateRemainingAmount(goalId);
    }

    /* 남은 기간 계산 */
    @GetMapping("/{goalId}/remaining-days")
    public long getRemainingDays(@PathVariable int goalId, @AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 남은 기간 계산 - userId : {}, goalId : {}", userId, goalId);
        return service.calculateRemainingDays(goalId);
    }

    /* 목표 키워드 조회 */
    @GetMapping("/{goalId}/keyword")
    public String getGoalKeyword(@PathVariable int goalId, @AuthenticationPrincipal CustomUser user){
        if (user == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int userId = user.getUser().getUserId();
        log.info("목표 키워드 조회 - userId : {}, goalId : {}", userId, goalId);
        return String.valueOf(service.getGoalKeyword(goalId));
    }
}
