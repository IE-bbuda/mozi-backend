package org.iebbuda.mozi.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.user.domain.UserVO;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageEditResponseDTO {
    @JsonProperty("login_id")
    private String loginId;        // 고정값 (수정 불가)

    private String username;       // 고정값 (수정 불가)

    private String email;          // 현재값 (수정 가능)

    @JsonProperty("phone_number")
    private String phoneNumber;    // 현재값 (수정 가능)

    public static MyPageEditResponseDTO of(UserVO userVO) {
        return MyPageEditResponseDTO.builder()
                .loginId(userVO.getLoginId())
                .username(userVO.getUsername())
                .email(userVO.getEmail())
                .phoneNumber(userVO.getPhoneNumber())
                .build();
    }
}
