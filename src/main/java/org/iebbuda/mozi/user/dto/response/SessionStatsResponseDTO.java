package org.iebbuda.mozi.user.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionStatsResponseDTO {
    private int activeSessions;
    private int totalSessions;

    public static SessionStatsResponseDTO of(int activeSessions, int totalSessions) {
        return SessionStatsResponseDTO.builder()
                .activeSessions(activeSessions)
                .totalSessions(totalSessions)
                .build();
    }
}
