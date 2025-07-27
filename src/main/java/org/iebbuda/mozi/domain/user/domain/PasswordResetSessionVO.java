package org.iebbuda.mozi.domain.user.domain;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PasswordResetSessionVO {
    private int id;
    private String token;
    private int userId;
    private LocalDateTime expiresAt;
    private boolean completed;
    private LocalDateTime createdAt;
}
