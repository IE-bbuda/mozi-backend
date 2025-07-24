package org.iebbuda.mozi.user.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.security.account.domain.AuthVO;
import org.iebbuda.mozi.user.domain.UserVO;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Data
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
    private String createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String birthDate;

    private List<AuthVO> authList;

    public static UserDTO of(UserVO vo){
        return UserDTO.builder()
                .userId(vo.getUserId())
                .loginId(vo.getLoginId())
                .username(vo.getUsername())
                .phoneNumber(vo.getPhoneNumber())
                .email(vo.getEmail())
                .createAt(vo.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updateAt(vo.getUpdateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .mainBank(vo.getMainBank())
                .birthDate(vo.getBirthDate().toString())
                .authList(vo.getAuthList())
                .build();
    }
    public UserVO toVO(){
        return UserVO.builder()
                .userId(userId)
                .loginId(loginId)
                .username(username)
                .phoneNumber(phoneNumber)
                .createAt(LocalDateTime.parse(createAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .createAt(LocalDateTime.parse(createAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .mainBank(mainBank)
                .birthDate(Date.valueOf(birthDate))
                .authList(authList)
                .build();
    }
}
