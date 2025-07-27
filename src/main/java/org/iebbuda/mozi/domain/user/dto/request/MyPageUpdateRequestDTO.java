package org.iebbuda.mozi.domain.user.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageUpdateRequestDTO {
    private String password;        // 새 비밀번호 (선택적)
    private String email;          // 새 이메일
    private String phoneNumber;    // 새 전화번호
}
