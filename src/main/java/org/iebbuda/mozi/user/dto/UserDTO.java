package org.iebbuda.mozi.user.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.iebbuda.mozi.security.account.domain.AuthVO;
import org.iebbuda.mozi.user.domain.UserVO;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private int userId;
    private String loginId;
    private String username;
    private String phoneNumber;
    private String email;
    private String mainBank;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatedAt;

    private String birthDate;

    private List<AuthVO> authList;

    public static UserDTO of(UserVO vo){
        return UserDTO.builder()
                .userId(vo.getUserId())
                .loginId(vo.getLoginId())
                .username(vo.getUsername())
                .phoneNumber(vo.getPhoneNumber())
                .email(vo.getEmail())
                .createdAt(vo.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updatedAt(vo.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .mainBank(vo.getMainBank())
                .birthDate(vo.getBirthDate())
                .authList(vo.getAuthList())
                .build();
    }

    public UserVO toVO(){
        UserVO userVO = new UserVO();
        userVO.setUserId(userId);
        userVO.setLoginId(loginId);
        userVO.setUsername(username);
        userVO.setPhoneNumber(phoneNumber);
        userVO.setEmail(email);
        userVO.setCreatedAt(LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        userVO.setUpdatedAt(LocalDateTime.parse(updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        userVO.setMainBank(mainBank);
        userVO.setBirthDate(birthDate);
        userVO.setAuthList(authList);

        return userVO;
    }
}
