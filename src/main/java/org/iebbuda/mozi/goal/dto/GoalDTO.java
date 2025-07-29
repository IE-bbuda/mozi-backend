package org.iebbuda.mozi.goal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.goal.domain.GoalVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalDTO {

    private int goalId;                 // 목표 고유 ID
    private int userId;                 // 사용자 고유 ID
    private String goalName;            // 목표명
    private GoalVO.GoalKeyword keyword;        // 목표키워드 (결혼, 취업, 내집마련, 여행, 학자금, 취미)
    private BigDecimal targetAmount;    // 목표금액
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime goalDate;     // 목표날짜
    private String memo;                // 메모
    private boolean goalStatus;         // 목표상태
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 생성날짜
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;    // 수정날짜

    public enum GoalKeyword {
        MARRIAGE,
        EMPLOYMENT,
        HOME_PURCHASE,
        TRAVEL,
        EDUCATION_FUND,
        HOBBY
    }

    // VO -> DTO
    public static GoalDTO of(GoalVO vo) {
        return vo == null ? null : GoalDTO.builder()
                .goalId(vo.getGoalId())
                .userId(vo.getUserId())
                .goalName(vo.getGoalName())
                .keyword(vo.getKeyword())
                .targetAmount(vo.getTargetAmount())
                .goalDate(vo.getGoalDate())
                .memo(vo.getMemo())
                .goalStatus(vo.isGoalStatus())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }

    // DTO -> VO
    public GoalVO toVo() {
        return GoalVO.builder()
                .goalId(goalId)
                .userId(userId)
                .goalName(goalName)
                .keyword(keyword)
                .targetAmount(targetAmount)
                .goalDate(goalDate)
                .memo(memo)
                .goalStatus(goalStatus)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

}
