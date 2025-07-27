package org.iebbuda.mozi.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetVerifyResponseDTO {
    private boolean verified;
    private String message;
    private String token;   //임시 토큰

    public static PasswordResetVerifyResponseDTO success(String token) {
        return PasswordResetVerifyResponseDTO.builder()
                .verified(true)
                .message("본인 확인이 완료되었습니다. 새 비밀번호를 설정해주세요")
                .token(token)
                .build();
    }

    // PasswordResetVerifyResponseDTO.java에 추가
    public static PasswordResetVerifyResponseDTO failed(String message) {
        return PasswordResetVerifyResponseDTO.builder()
                .verified(false)
                .message(message)
                .token(null)
                .build();
    }

    public static PasswordResetVerifyResponseDTO failed() {
        return failed("입력하신 정보로 가입된 계정이 없습니다.");
    }
}

