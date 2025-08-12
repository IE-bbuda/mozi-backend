package org.iebbuda.mozi.domain.user.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRecoveryRequestDTO {
    private String loginId;
    private String email;
    private String password;  // 일반 로그인 사용자용
}
