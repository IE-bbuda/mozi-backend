package org.iebbuda.mozi.user.domain;


import lombok.Data;
import org.iebbuda.mozi.security.account.domain.AuthVO;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private int userId;
    private String loginId;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String mainBank;
    private String birthDate;

    private List<AuthVO> authList;
}
