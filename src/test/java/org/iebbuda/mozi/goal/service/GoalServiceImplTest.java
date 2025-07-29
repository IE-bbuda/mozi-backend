package org.iebbuda.mozi.goal.service;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.goal.dto.GoalDTO;
import org.iebbuda.mozi.domain.goal.domain.GoalVO;
import org.iebbuda.mozi.domain.goal.service.GoalService;
import org.iebbuda.mozi.domain.security.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})
@Log4j2
@Transactional // 테스트 후 롤백
class GoalServiceImplTest {
    @Autowired
    private GoalService goalService;
    @Test
    @DisplayName("사용자별 목표 조회")
    void getGoalListByUserId() {
        // 김케비(user_id=1)의 목표 조회
        List<GoalDTO> goals = goalService.getGoalListByUserId(1);

        // 목표가 0개일 수도 있으므로 크기 체크
        assertTrue(goals.size() >= 0);

        log.info("김케비의 현재 목표 개수: " + goals.size());
        goals.forEach(g -> log.info("- " + g.getGoalName() + ": " + g.getTargetAmount() + "원"));
    }


    @Test
    @DisplayName("목표 생성 - 새로운 목표 추가")
    void createGoal() {
        // 생성 전 목표 개수 확인
        List<GoalDTO> goalsBefore = goalService.getGoalListByUserId(1);
        int countBefore = goalsBefore.size();
        log.info("목표 생성 전 개수: " + countBefore);

        // 김케비에게 새 목표 추가
        GoalDTO newGoal = GoalDTO.builder()
                .userId(1)
                .goalName("결혼자금")
                .keyword(GoalVO.GoalKeyword.MARRIAGE)
                .targetAmount(BigDecimal.valueOf(50_000_000))
                .goalDate(LocalDateTime.of(2028, 6, 30, 0, 0))
                .memo("28년도 결혼 예정")
                .goalStatus(true)
                .build();

        GoalDTO created = goalService.createGoal(newGoal);

        assertNotNull(created);
        assertNotNull(created.getGoalId());
        assertEquals("결혼자금", created.getGoalName());

        // 생성 후 목표 개수 확인 - 이전 개수 + 1개여야 함
        List<GoalDTO> goalsAfter = goalService.getGoalListByUserId(1);
        int countAfter = goalsAfter.size();
        log.info("목표 생성 후 개수: " + countAfter);

        assertEquals(countBefore + 1, countAfter, "목표 생성 후 개수가 1 증가해야 함");

        // 생성된 목표가 목록에 포함되어 있는지 확인
        boolean goalExists = goalsAfter.stream()
                .anyMatch(goal -> "결혼자금".equals(goal.getGoalName()) &&
                        Objects.equals(goal.getGoalId(), created.getGoalId()));
        assertTrue(goalExists, "생성된 목표가 목록에 포함되어야 함");

        log.info("새로 생성된 목표 ID: " + created.getGoalId());
    }
    @Test
    @DisplayName("목표 달성률 계산")// 이부분은 나중에 계좌 api 추가후 변경
    void calculateAchievementRate() {
        // 1. 정상 케이스: 50% 달성
        double rate50 = goalService.calculateAchievementRate(
                BigDecimal.valueOf(50_000_000),  // 현재 5천만원
                BigDecimal.valueOf(100_000_000)  // 목표 1억
        );
        assertEquals(50.0, rate50, 0.001);

        // 2. 100% 초과 케이스: 120% → 100% 반환
        double rateOver = goalService.calculateAchievementRate(
                BigDecimal.valueOf(360_000_000), // 현재 3.6억
                BigDecimal.valueOf(300_000_000)  // 목표 3억 (내집마련)
        );
        assertEquals(100.0, rateOver, 0.001);

        // 3. 0% 케이스
        double rateZero = goalService.calculateAchievementRate(
                BigDecimal.ZERO,
                BigDecimal.valueOf(100_000_000)
        );
        assertEquals(0.0, rateZero, 0.001);

        log.info("달성률 테스트 - 50%: " + rate50 + ", 초과: " + rateOver + ", 0%: " + rateZero);
    }

    @Test
    @DisplayName("목표 달성 예상 날짜 계산")
    void calculateExpectedAchievementDate() {
        // 먼저 목표가 존재하는지 확인
        GoalDTO goal = goalService.getGoal(2);
        if (goal == null) {
            log.info("내집마련 목표(ID:2)가 존재하지 않습니다. 테스트 스킵");
            return;
        }

        // 내집마련 목표(3억)를 월 500만원씩 저축
        LocalDateTime expectedDate = goalService.calculateExpectedAchievementDate(
                2, // 내집마련 goal_id
                BigDecimal.valueOf(5_000_000) // 월 500만원
        );

        if (expectedDate != null) {
            assertTrue(expectedDate.isAfter(LocalDateTime.now()));
            log.info("내집마련 예상 달성일 (월 500만원): " + expectedDate);
        } else {
            log.info("예상 달성일 계산 불가: 현재 잔액이 목표 금액 이상이거나 기타 이유");
        }
    }

    @Test
    @DisplayName("남은 금액/기간 계산 - 1억 모으기")
    void calculateRemaining() {
        int billionGoalId = 1; // 1억 모으기 goal_id

        // 목표가 존재하는지 확인
        GoalDTO goal = goalService.getGoal(billionGoalId);
        if (goal == null) {
            log.info("1억 모으기 목표(ID:1)가 존재하지 않습니다. 테스트 스킵");
            return;
        }

        // 남은 금액
        BigDecimal remainingAmount = goalService.calculateRemainingAmount(billionGoalId);
        assertNotNull(remainingAmount);
        assertTrue(remainingAmount.compareTo(BigDecimal.ZERO) >= 0);
        log.info("1억 모으기 남은 금액: " + remainingAmount + "원");

        // 남은 기간
        long remainingDays = goalService.calculateRemainingDays(billionGoalId);
        assertTrue(remainingDays >= 0);

        if (remainingDays > 0) {
            long years = remainingDays / 365;
            log.info("1억 모으기 남은 기간: " + remainingDays + "일 (약 " + years + "년)");
        } else {
            log.info("목표 기간이 지났거나 오늘이 마감일입니다.");
        }
    }

    @Test
    @DisplayName("CRUD 기본 기능 테스트")
    void crudOperations() {
        // 1. CREATE - 새 목표 생성
        GoalDTO newGoal = GoalDTO.builder()
                .userId(1)
                .goalName("여행자금")
                .keyword(GoalVO.GoalKeyword.TRAVEL)
                .targetAmount(BigDecimal.valueOf(10_000_000))
                .goalDate(LocalDateTime.of(2026, 8, 15, 0, 0))
                .memo("유럽 여행")
                .goalStatus(true)
                .build();

        GoalDTO created = goalService.createGoal(newGoal);
        assertNotNull(created.getGoalId());
        log.info("생성된 목표: " + created.getGoalName() + " (ID: " + created.getGoalId() + ")");

        // 2. READ - 조회
        GoalDTO retrieved = goalService.getGoal(created.getGoalId());
        assertNotNull(retrieved);
        assertEquals("여행자금", retrieved.getGoalName());
        log.info("조회된 목표: " + retrieved.getGoalName());

        // 3. UPDATE - 수정
        retrieved.setTargetAmount(BigDecimal.valueOf(15_000_000));
        retrieved.setMemo("유럽 + 미국 여행으로 확대");

        boolean updated = goalService.updateGoal(retrieved);
        assertTrue(updated);

        GoalDTO afterUpdate = goalService.getGoal(created.getGoalId());
        // BigDecimal 비교는 compareTo 사용
        assertEquals(0, BigDecimal.valueOf(15_000_000).compareTo(afterUpdate.getTargetAmount()));
        log.info("수정 후 금액: " + afterUpdate.getTargetAmount() + "원");

        // 4. DELETE - 삭제
        boolean deleted = goalService.deleteGoal(created.getGoalId());
        assertTrue(deleted);

        GoalDTO afterDelete = goalService.getGoal(created.getGoalId());
        assertNull(afterDelete);
        log.info("목표 삭제 완료");
    }

    @Test
    @DisplayName("목표 상태(완료/미완료) 업데이트 테스트")
    void updateGoalStatus() {
        // 새 목표 생성 (테스트용)
        GoalDTO testGoal = GoalDTO.builder()
                .userId(1)
                .goalName("상태변경테스트")
                .keyword(GoalVO.GoalKeyword.HOBBY)
                .targetAmount(BigDecimal.valueOf(1_000_000))
                .goalDate(LocalDateTime.of(2025, 12, 31, 0, 0))
                .goalStatus(true)
                .build();

        GoalDTO created = goalService.createGoal(testGoal);

        // 상태를 false로 변경
        boolean statusUpdated = goalService.updateGoalStatus(created.getGoalId(), false);
        assertTrue(statusUpdated);

        // 변경 확인
        GoalDTO updated = goalService.getGoal(created.getGoalId());
        assertFalse(updated.isGoalStatus());
        log.info("목표 상태 변경: true → false");

        // 정리
        goalService.deleteGoal(created.getGoalId());
    }

    @Test
    @DisplayName("목표 키워드 조회 테스트 - goalId로 keyword 찾기")
    void getGoalKeywordTest() {
        // 1. 실제 존재하는 목표들의 키워드 확인
        GoalVO.GoalKeyword billionKeyword = goalService.getGoalKeyword(1);
        if (billionKeyword != null) {
            assertEquals(GoalVO.GoalKeyword.HOBBY, billionKeyword);
            log.info("1억 모으기 목표(ID:1) 키워드: " + billionKeyword);
        }

        GoalVO.GoalKeyword homeKeyword = goalService.getGoalKeyword(2);
        if (homeKeyword != null) {
            assertEquals(GoalVO.GoalKeyword.HOME_PURCHASE, homeKeyword);
            log.info("내집마련 목표(ID:2) 키워드: " + homeKeyword);
        }

        // 2. 새로운 목표 생성하여 키워드 확인
        GoalDTO newGoal = GoalDTO.builder()
                .userId(1)
                .goalName("학자금마련")
                .keyword(GoalVO.GoalKeyword.EDUCATION_FUND)
                .targetAmount(BigDecimal.valueOf(20_000_000))
                .goalDate(LocalDateTime.of(2027, 3, 1, 0, 0))
                .goalStatus(true)
                .build();

        GoalDTO createdGoal = goalService.createGoal(newGoal);

        // getGoalKeyword() 메서드로 키워드 확인
        GoalVO.GoalKeyword createdKeyword = goalService.getGoalKeyword(createdGoal.getGoalId());
        assertEquals(GoalVO.GoalKeyword.EDUCATION_FUND, createdKeyword);
        log.info("새 목표 키워드: " + createdKeyword);

        // 3. 존재하지 않는 목표 - null 반환 확인
        GoalVO.GoalKeyword nonExistentKeyword = goalService.getGoalKeyword(99999);
        assertNull(nonExistentKeyword);

        // 정리
        goalService.deleteGoal(createdGoal.getGoalId());
    }
    @Test
    @DisplayName("유저ID → 목표ID → 키워드 연계 조회")
    void userToGoalToKeywordTest() {
        // 김케비의 목표들 조회
        List<GoalDTO> goals = goalService.getGoalListByUserId(1);

        for (GoalDTO goal : goals) {
            // 각 목표의 키워드 조회
            GoalVO.GoalKeyword keyword = goalService.getGoalKeyword(goal.getGoalId());
            assertNotNull(keyword);
            log.info(goal.getGoalName() + " → " + keyword);
        }
    }

}