package org.iebbuda.mozi.domain.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInfoStatusDTO {

    @JsonProperty("has_personal_info")
    private boolean hasPersonalInfo;        // 퍼스널 정보 입력 여부

    @JsonProperty("created_at")
    private LocalDateTime createdAt;        // 가입일

    @JsonProperty("days_remaining")
    private Integer daysRemaining;          // 남은 일수 (14일 기준)

    @JsonProperty("needs_prompt")
    private boolean needsPrompt;            // 프롬프트 표시 필요 여부

}
