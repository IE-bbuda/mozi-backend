package org.iebbuda.mozi.domain.security.dto.oauth;

public interface OAuthUserInfo {
    String getProviderId();
    String getNickname();
    String getEmail();
    OAuthProvider getProvider();
}
