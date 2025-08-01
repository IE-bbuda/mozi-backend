package org.iebbuda.mozi.domain.user.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.iebbuda.mozi.domain.security.account.domain.AuthVO;
import org.iebbuda.mozi.domain.user.domain.UserVO;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("login_id")
    private String loginId;

    private String username;

    @JsonProperty("phone_number")
    private String phoneNumber;
    private String email;

    @JsonProperty("main_bank")
    private String mainBank;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private String createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
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
