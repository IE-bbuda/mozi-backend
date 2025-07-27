package org.iebbuda.mozi.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.profile.domain.UserProfileVO;
import org.iebbuda.mozi.profile.dto.UserProfileInfoDTO;
import org.iebbuda.mozi.user.domain.UserVO;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponseDTO {
    @JsonProperty("login_id")
    private String loginId;

    private String username;
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("has_personal_info")
    private boolean hasPersonalInfo;

    @JsonProperty("personal_info")
    private UserProfileInfoDTO personalInfo;

    public static MyPageResponseDTO of(UserVO userVO, UserProfileVO userProfileVO) {
        return MyPageResponseDTO.builder()
                .loginId(userVO.getLoginId())
                .username(userVO.getUsername())
                .email(userVO.getEmail())
                .phoneNumber(userVO.getPhoneNumber())
                .hasPersonalInfo(userProfileVO != null)
                .personalInfo(createPersonalInfo(userProfileVO))
                .build();
    }

    private static UserProfileInfoDTO createPersonalInfo(UserProfileVO userProfileVO) {
        if (userProfileVO == null) {
            return null;
        }
        return UserProfileInfoDTO.of(userProfileVO);
    }
}
