package org.iebbuda.mozi.domain.security.service.oauth;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.security.dto.oauth.GoogleUserInfoDTO;
import org.iebbuda.mozi.domain.security.dto.oauth.OAuthUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Log4j2
@PropertySource("classpath:application.properties")
public class GoogleOAuthService implements OAuthService{

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getLoginUrl() {
        return UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/auth")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "openid email profile")
                .toUriString();
    }

    @Override
    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, String.class);
            return extractAccessToken(response.getBody());
        } catch (Exception e) {
            log.error("구글 액세스 토큰 요청 실패", e);
            throw new BaseException(BaseResponseStatus.GOOGLE_TOKEN_REQUEST_FAILED);
        }
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<GoogleUserInfoDTO> response = restTemplate.exchange(
                    GOOGLE_USER_INFO_URL,
                    HttpMethod.GET,
                    request,
                    GoogleUserInfoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("구글 사용자 정보 요청 실패", e);
            throw new BaseException(BaseResponseStatus.GOOGLE_USER_INFO_REQUEST_FAILED);
        }
    }

    @Override
    public boolean supports(String provider) {
        return "GOOGLE".equalsIgnoreCase(provider);
    }

    private String extractAccessToken(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String accessToken = rootNode.path("access_token").asText();

            if (accessToken.isEmpty()) {
                throw new BaseException(BaseResponseStatus.GOOGLE_TOKEN_EXTRACT_FAILED);
            }

            return accessToken;
        } catch (Exception e) {
            log.error("구글 access_token 파싱 실패", e);
            throw new BaseException(BaseResponseStatus.GOOGLE_TOKEN_EXTRACT_FAILED);
        }
    }
}
