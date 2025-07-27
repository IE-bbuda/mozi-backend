package org.iebbuda.mozi.domain.user.dto.request;


import lombok.*;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinRequestDTO {
    private String loginId;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private String birthDate;   // "010203" 형태로 받음


    public UserVO toVO(PasswordEncoder passwordEncoder){
        LocalDateTime now = LocalDateTime.now();
        UserVO userVO = new UserVO();
        userVO.setLoginId(loginId);
        userVO.setUsername(username);
        userVO.setPassword(passwordEncoder.encode(password));
        userVO.setPhoneNumber(phoneNumber);
        userVO.setEmail(email);
        userVO.setCreatedAt(now);
        userVO.setUpdatedAt(now);
        userVO.setBirthDate(birthDate);
        return userVO;
    }
}
