package org.iebbuda.mozi.domain.security.service.oauth;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.security.dto.oauth.KakaoUserInfoDTO;
import org.iebbuda.mozi.domain.security.dto.oauth.OAuthProvider;
import org.iebbuda.mozi.domain.security.dto.oauth.OAuthUserInfo;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
@PropertySource("classpath:application.properties")
public class KakaoOAuthService implements OAuthService{

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, String.class);
            String responseBody = response.getBody();
            String accessToken = extractAccessToken(responseBody);
            return accessToken;
        } catch (Exception e) {
            log.error("카카오 액세스 토큰 요청 실패", e);
            throw new BaseException(BaseResponseStatus.KAKAO_TOKEN_REQUEST_FAILED);
        }
    }


    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfoDTO> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    request,
                    KakaoUserInfoDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 실패", e);
            throw new BaseException(BaseResponseStatus.KAKAO_USER_INFO_REQUEST_FAILED);
        }
    }

    @Override
    public boolean supports(String provider) {
        return "KAKAO".equalsIgnoreCase(provider);
    }

    private String extractAccessToken(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String accessToken = rootNode.path("access_token").asText();

            if (accessToken.isEmpty()) {
                throw new BaseException(BaseResponseStatus.KAKAO_TOKEN_EXTRACT_FAILED);
            }

            return accessToken;
        } catch (Exception e) {
            log.error("access_token 파싱 실패", e);
            throw new BaseException(BaseResponseStatus.KAKAO_TOKEN_EXTRACT_FAILED);
        }
    }


}
