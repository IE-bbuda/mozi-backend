package org.iebbuda.mozi.domain.security.service.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuthServiceFactory {

    private final List<OAuthService> oAuthServices;

    public OAuthService getOAuthService(String provider){
        return oAuthServices.stream()
                .filter(service -> service.supports(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다." + provider));
    }
}
