package org.iebbuda.mozi.profile.domain;


import lombok.Data;
import org.iebbuda.mozi.profile.domain.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserProfileVO {
    // DB에는 String으로 저장되지만, 코드에서는 enum으로 사용
    int userId;
    private Region region;
    private Integer age;
    private MaritalStatus maritalStatus;
    private BigDecimal annualIncome;
    private EducationLevel educationLevel;
    private EmploymentStatus employmentStatus;
    private Major major;
    private Specialty specialty;
}
