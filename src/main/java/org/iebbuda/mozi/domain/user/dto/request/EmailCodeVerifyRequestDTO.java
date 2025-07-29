package org.iebbuda.mozi.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailCodeVerifyRequestDTO {
    private String email;
    private String verificationCode;
}
