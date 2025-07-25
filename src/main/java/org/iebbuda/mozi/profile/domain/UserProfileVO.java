package org.iebbuda.mozi.profile.domain;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserProfileVO {
    private int userId;                    // user_id (PK, FK)
    private String region;                 // 지역 (ENUM)
    private Integer age;                   // 나이
    private String maritalStatus;          // 결혼상태 (ENUM)
    private BigDecimal annualIncome;       // 연간소득 (decimal(15,2))
    private String educationLevel;         // 교육수준 (ENUM)
    private String employmentStatus;       // 고용상태 (ENUM)
    private String major;                  // 전공 (ENUM)
    private String specialty;              // 특기 (ENUM)

    // 공통 필드 (만약 있다면)
    private LocalDateTime createdAt;       // 생성일시
    private LocalDateTime updatedAt;       // 수정일시
}
