package org.iebbuda.mozi.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetResponseDTO {
    private boolean success;
    private String message;

    public static PasswordResetResponseDTO success() {
        return PasswordResetResponseDTO.builder()
                .success(true)
                .message("비밀번호가 성공적으로 변경되었습니다.")
                .build();
    }

    public static PasswordResetResponseDTO failed(String message) {
        return PasswordResetResponseDTO.builder()
                .success(false)
                .message(message)
                .build();
    }
    public static PasswordResetResponseDTO failed() {
        return PasswordResetResponseDTO.builder()
                .success(false)
                .message("유효하지 않거나 만료된 요청입니다.")
                .build();
    }
}
