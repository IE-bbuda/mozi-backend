package org.iebbuda.mozi.domain.user.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResultDTO {
    private boolean success;
    private String message;
    private String withdrawalType;
    private boolean isRecoverable;
    private LocalDateTime recoveryDeadline;
    private List<String> nextSteps;
}
