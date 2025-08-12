package org.iebbuda.mozi.domain.user.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.iebbuda.mozi.domain.user.domain.UserVO;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalInfoDTO {
    /**
     * 사용자 타입 (비즈니스 로직)
     */
    @JsonProperty("user_type")
    private String userType;  // "REGULAR" | "OAUTH"

    /**
     * OAuth 제공자 코드 (DB 데이터)
     */
    @JsonProperty("provider_code")
    private String providerCode;  // "KAKAO" | "GOOGLE" | "NAVER" | null

    /**
     * 핵심 사용자 데이터 (DB 조회 필요)
     */
    @JsonProperty("user_data")
    private UserDataSummary userData;

    @Data
    @Builder
    public static class UserDataSummary {
        private String email;           // 마스킹된 이메일
        private String joinDate;        // YYYY-MM-DD 형식
        private int scrapCount;         // DB에서 실제 조회
        private int goalCount;          // DB에서 실제 조회
    }


    public static WithdrawalInfoDTO of(UserVO user, int scrapCount, int goalCount) {
        boolean isOAuth = user.getProvider() != null;

        String userType;
        if (isOAuth) {
            userType = "OAUTH";
        } else {
            userType = "REGULAR";
        }

        return WithdrawalInfoDTO.builder()
                .userType(userType)
                .providerCode(user.getProvider())
                .userData(UserDataSummary.builder()
                        .email(maskEmail(user.getEmail()))
                        .joinDate(user.getCreatedAt().toLocalDate().toString())
                        .scrapCount(scrapCount)
                        .goalCount(goalCount)
                        .build())
                .build();
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return username + "@" + domain;
        }

        return username.substring(0, 2) + "*".repeat(username.length() - 2) + "@" + domain;
    }
}
