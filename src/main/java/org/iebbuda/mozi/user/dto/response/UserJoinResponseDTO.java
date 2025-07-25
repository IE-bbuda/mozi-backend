package org.iebbuda.mozi.user.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import org.iebbuda.mozi.user.domain.UserVO;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinResponseDTO {

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("login_id")
    private String loginId;

    private String username;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private String createdAt;

    public static UserJoinResponseDTO of(UserVO vo){
        return UserJoinResponseDTO.builder()
                .userId(vo.getUserId())
                .loginId(vo.getLoginId())
                .username(vo.getUsername())
                .email(vo.getEmail())
                .createdAt(vo.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

    }
}
