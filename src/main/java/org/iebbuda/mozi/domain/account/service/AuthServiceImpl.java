package org.iebbuda.mozi.domain.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.account.dto.AuthDTO;
import org.iebbuda.mozi.domain.account.dto.AuthResponseDTO;
import org.iebbuda.mozi.domain.account.external.ExternalApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
@PropertySource({"classpath:/application.properties"})
public class AuthServiceImpl implements AuthService {

    private final ExternalApiClient externalApiClient;

    private AuthDTO cachedToken;

    @Value("${codef.clientId}") private String clientId;

    @Value("${codef.clientSecret}") private String clientSecret;

    @Override
    public String getAccessToken() {
        if (cachedToken == null || cachedToken.isExpired()) {
            log.info("토큰이 없거나 만료되어 새로 요청합니다.");
            cachedToken = requestNewToken();
        } else {
            log.info("유효한 토큰 재사용");
        }
        return cachedToken.getToken();
    }

    private AuthDTO requestNewToken() {
        String url = "https://oauth.codef.io/oauth/token";
        String auth = clientId + ":" + clientSecret;
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        Map<String, String> headers = Map.of(
                "Content-Type", "application/x-www-form-urlencoded",
                "Authorization", authHeader
        );
        String body = "grant_type=client_credentials&scope=read";

        AuthResponseDTO response = externalApiClient.post(
                url, headers, body, AuthResponseDTO.class
        ).getBody();

        if (response == null || response.getAccessToken() == null) {
            throw new RuntimeException("토큰 응답이 null입니다");
        }

        return AuthDTO.from(response);
    }
}
