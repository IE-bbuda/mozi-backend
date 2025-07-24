package org.iebbuda.mozi.user.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.iebbuda.mozi.security.account.domain.AuthVO;
import org.iebbuda.mozi.user.domain.UserVO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinResponseDTO {

    private int userId;
    private String loginId;
    private String username;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
