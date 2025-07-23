package org.iebbuda.mozi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.user.domain.UserVO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinDTO {
    private String loginId;
    private String password;
    private String phoneNumber;
    private String email;


    public UserVO toVO(){
        return UserVO.builder()
                .loginId(loginId)
                .password(password)
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
    }
}
