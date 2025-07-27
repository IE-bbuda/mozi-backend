package org.iebbuda.mozi.domain.security.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.domain.security.account.domain.AuthVO;
import org.iebbuda.mozi.domain.user.domain.UserVO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    @JsonProperty("login_id")
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
