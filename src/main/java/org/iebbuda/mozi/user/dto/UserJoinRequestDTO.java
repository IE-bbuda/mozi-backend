package org.iebbuda.mozi.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.user.domain.UserVO;


import java.sql.Date;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinRequestDTO {
    private String loginId;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String birthDate;   // "1990-01-15" 형태로 받음


    public UserVO toVO(){
        LocalDateTime now = LocalDateTime.now();
        return UserVO.builder()
                .loginId(loginId)
                .username(username)
                .password(password)
                .phoneNumber(phoneNumber)
                .email(email)
                .createAt(now)
                .updateAt(now)
                .birthDate(Date.valueOf(birthDate))
                .build();
    }
}
