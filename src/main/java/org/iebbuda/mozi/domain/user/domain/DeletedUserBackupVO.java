package org.iebbuda.mozi.domain.user.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletedUserBackupVO {
    private Long id;
    private Integer originalUserId;
    private String originalLoginId;
    private String originalUsername;
    private String originalEmail;
    private String originalPassword;
    private String originalPhoneNumber;
    private String originalBirthDate;
    private String originalProvider;
    private String originalProviderId;
    private String userType;
    private String withdrawalType;
    private String withdrawalReason;
    private Boolean isRecoverable;
    private LocalDateTime recoveryDeadline;
    private LocalDateTime originalCreatedAt;
    private LocalDateTime deletedAt;
}
