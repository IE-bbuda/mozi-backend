package org.iebbuda.mozi.domain.security.dto.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoogleUserInfoDTO implements OAuthUserInfo{
    private String id;
    private String email;
    private String name;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    private String picture;
    private String locale;

    @JsonProperty("verified_email")
    private Boolean verifiedEmail;


    @Override
    public String getProviderId() {
        return this.id;
    }

    @Override
    public String getNickname() {
        // 구글의 경우 별도 닉네임이 없으므로 이름을 사용
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.GOOGLE;
    }
}
