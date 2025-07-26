package org.iebbuda.mozi.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.profile.domain.UserProfileVO;
import org.iebbuda.mozi.profile.domain.enums.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInfoDTO {
    private Region region;              // enum 직접 사용
    private Integer age;

    @JsonProperty("marital_status")
    private MaritalStatus maritalStatus; // enum 직접 사용

    @JsonProperty("annual_income")
    private BigDecimal annualIncome;     // BigDecimal 직접 사용

    @JsonProperty("education_level")
    private EducationLevel educationLevel;

    @JsonProperty("employment_status")
    private EmploymentStatus employmentStatus;

    private Major major;
    private Specialty specialty;

    public static PersonalInfoDTO of(UserProfileVO vo) {
        if (vo == null) return null;

        return PersonalInfoDTO.builder()
                .region(vo.getRegion())
                .age(vo.getAge())
                .maritalStatus(vo.getMaritalStatus())
                .annualIncome(vo.getAnnualIncome())
                .educationLevel(vo.getEducationLevel())
                .employmentStatus(vo.getEmploymentStatus())
                .major(vo.getMajor())
                .specialty(vo.getSpecialty())
                .build();
    }

    // 유효성 검증 메서드들
    public boolean isStep1Valid() {
        return region != null && age != null && maritalStatus != null && annualIncome != null;
    }

    public boolean isStep2Valid() {
        return educationLevel != null;
    }

    public boolean isStep3Valid() {
        return employmentStatus != null;
    }

    public boolean isStep4Valid() {
        return major != null;
    }

    public boolean isStep5Valid() {
        return specialty != null;
    }

    public boolean isCompletelyValid() {
        return isStep1Valid() && isStep2Valid() && isStep3Valid() && isStep4Valid() && isStep5Valid();
    }
}
