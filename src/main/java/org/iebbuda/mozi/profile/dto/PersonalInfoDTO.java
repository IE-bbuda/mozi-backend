package org.iebbuda.mozi.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.profile.domain.UserProfileVO;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInfoDTO {

    private String region;              // 지역
    private Integer age;                // 나이

    @JsonProperty("marital_status")
    private String maritalStatus;       // 결혼상태

    @JsonProperty("annual_income")
    private String annualIncome;        // 연간소득

    @JsonProperty("education_level")
    private String educationLevel;      // 교육수준

    @JsonProperty("employment_status")
    private String employmentStatus;    // 고용상태

    private String major;               // 전공
    private String specialty;           // 특기

    public static PersonalInfoDTO of(UserProfileVO vo) {
        if (vo == null) return null;

        String annualIncomeStr = null;
        if (vo.getAnnualIncome() != null) {
            annualIncomeStr = vo.getAnnualIncome().toString();
        }

        return PersonalInfoDTO.builder()
                .region(vo.getRegion())
                .age(vo.getAge())
                .maritalStatus(vo.getMaritalStatus())
                .annualIncome(annualIncomeStr)
                .educationLevel(vo.getEducationLevel())
                .employmentStatus(vo.getEmploymentStatus())
                .major(vo.getMajor())
                .specialty(vo.getSpecialty())
                .build();
    }
}
