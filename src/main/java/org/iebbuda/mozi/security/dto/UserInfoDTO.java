package org.iebbuda.mozi.security.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.security.account.domain.AuthVO;
import org.iebbuda.mozi.user.domain.UserVO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    String loginId;
    String email;
    List<String> roles;

    public static UserInfoDTO of(UserVO user){
        return new UserInfoDTO(
                user.getLoginId(),
                user.getEmail(),
                user.getAuthList().stream()
                        .map(AuthVO::getAuth)
                        .toList()
        );
    }
}
