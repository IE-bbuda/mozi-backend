package org.iebbuda.mozi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.user.domain.UserVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinRequestDTO {
    private String loginId;
    private String password;
    private String phoneNumber;
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String birthDate;   // "1990-01-15" 형태로 받음


    public UserVO toVO(){
        return UserVO.builder()
                .loginId(loginId)
                .password(password)
                .phoneNumber(phoneNumber)
                .email(email)
                .birthDate(Date.valueOf(birthDate))
                .build();
    }
}
