package org.iebbuda.mozi.domain.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.domain.enums.*;
import org.iebbuda.mozi.profile.domain.enums.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileInfoDTO {
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

    public static UserProfileInfoDTO of(UserProfileVO vo) {
        if (vo == null) return null;

        return UserProfileInfoDTO.builder()
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

    public UserProfileVO toVO(int userId){
        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(userId);
        vo.setRegion(region);           // this 사용
        vo.setAge(age);
        vo.setMaritalStatus(maritalStatus);
        vo.setAnnualIncome(annualIncome); // this 사용
        vo.setEducationLevel(educationLevel);
        vo.setEmploymentStatus(employmentStatus);
        vo.setMajor(major);
        vo.setSpecialty(specialty);
        return vo;
    }
}
