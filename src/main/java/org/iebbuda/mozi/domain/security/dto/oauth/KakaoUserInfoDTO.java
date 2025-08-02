package org.iebbuda.mozi.domain.security.dto.oauth;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoUserInfoDTO implements OAuthUserInfo{
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonProperty("properties")
    private Properties properties;


    @Data
    public static class KakaoAccount {
        private String email;

        @JsonProperty("email_verified")
        private Boolean emailVerified;

        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;

        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;
    }

    @Data
    public static class Properties {
        private String nickname;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(this.id);
    }

    @Override
    public String getNickname() {
        if (properties != null && properties.getNickname() != null) {
            return properties.getNickname();
        }
        return null;
    }

    @Override
    public String getEmail() {
        if (kakaoAccount != null && kakaoAccount.getEmail() != null) {
            return kakaoAccount.getEmail();
        }
        return null;
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }
}
