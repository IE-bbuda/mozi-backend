package org.iebbuda.mozi.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    public static AuthDTO from(AuthResponseDTO response) {
        LocalDateTime now = LocalDateTime.now();
        return new AuthDTO(
                response.getAccessToken(),
                now,
                now.plusSeconds(response.getExpiresIn())
        );
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }
}
