package org.iebbuda.mozi.domain.security.service.oauth;

import org.iebbuda.mozi.domain.security.dto.oauth.OAuthUserInfo;

public interface OAuthService {
    String getAccessToken(String code);
    OAuthUserInfo getUserInfo(String accessToken);
    boolean supports(String provider);
    String getLoginUrl(); // 추가
}
