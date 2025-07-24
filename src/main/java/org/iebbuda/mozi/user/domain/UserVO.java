package org.iebbuda.mozi.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iebbuda.mozi.security.account.domain.AuthVO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {
    private int userId;
    private String loginId;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String mainBank;
    private Date birthDate;

    private List<AuthVO> authList;
}
