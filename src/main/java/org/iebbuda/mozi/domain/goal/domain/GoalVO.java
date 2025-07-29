package org.iebbuda.mozi.domain.goal.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.javassist.compiler.ast.Keyword;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalVO {

    // TABLE : UserGoal
    // goal_id(int), user_id(int), goal_name(varchar), keyword(enum), target_amount(decimal),
    // goal_date(datetime), memo(text), goal_status(boolean), created_at(datetime), updated_at(datetime)

    private int goalId;                 // 목표 고유 ID
    private int userId;                 // 사용자 고유 ID
    private String goalName;            // 목표명
    private GoalKeyword keyword;        // 목표키워드 (결혼, 취업, 내집마련, 여행, 학자금, 취미)
    private BigDecimal targetAmount;    // 목표금액
    private LocalDateTime goalDate;     // 목표날짜
    private String memo;                // 메모
    private boolean goalStatus;         // 목표상태
    private LocalDateTime createdAt;    // 생성날짜
    private LocalDateTime updatedAt;    // 수정날짜

    public enum GoalKeyword {
        MARRIAGE,
        EMPLOYMENT,
        HOME_PURCHASE,
        TRAVEL,
        EDUCATION_FUND,
        HOBBY
    }

}
