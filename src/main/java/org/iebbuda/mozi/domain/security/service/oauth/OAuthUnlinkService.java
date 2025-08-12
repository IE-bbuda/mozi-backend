package org.iebbuda.mozi.domain.security.service.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Log4j2
@PropertySource("classpath:application.properties")
public class OAuthUnlinkService {

    private final RestTemplate restTemplate = new RestTemplate();

    // 카카오 설정 (완전 구현)
    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.admin.key}")
    private String kakaoAdminKey;

    // 구글, 네이버 설정 (TODO - 나중에 구현)
    @Value("${google.client.id:}")
    private String googleClientId;

    @Value("${google.client.secret:}")
    private String googleClientSecret;

    @Value("${naver.client.id:}")
    private String naverClientId;

    @Value("${naver.client.secret:}")
    private String naverClientSecret;

    /**
     * OAuth 연동 해제 통합 메서드
     */
    public boolean unlinkOAuth(String provider, String providerId) {
        log.info("OAuth 연동 해제 시작 - provider: {}, providerId: {}", provider, providerId);

        try {
            switch (provider.toUpperCase()) {
                case "KAKAO":
                    return unlinkKakao(providerId);  // 실제 구현
                case "GOOGLE":
                    return unlinkGoogle(providerId); // 시뮬레이션
                case "NAVER":
                    return unlinkNaver(providerId);  // 시뮬레이션
                default:
                    log.warn("지원하지 않는 OAuth 제공자 - provider: {}", provider);
                    return false;
            }
        } catch (Exception e) {
            log.error("OAuth 연동 해제 실패 - provider: {}, providerId: {}", provider, providerId, e);
            return false;
        }
    }

    /**
     * 카카오 연동 해제 - 실제 구현
     */
    private boolean unlinkKakao(String providerId) {
        try {
            String url = "https://kapi.kakao.com/v1/user/unlink";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("target_id_type", "user_id");
            params.add("target_id", providerId);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("카카오 연동 해제 성공 - providerId: {}", providerId);
                return true;
            } else {
                log.warn("카카오 연동 해제 실패 - providerId: {}, status: {}", providerId, response.getStatusCode());
                return false;
            }

        } catch (Exception e) {
            log.error("카카오 연동 해제 API 호출 실패 - providerId: {}", providerId, e);
            return false;
        }
    }

    /**
     * 구글 연동 해제 - 시뮬레이션 (실제 구현 필요)
     *
     *  구글 연동 해제 구현의 어려움:
     * 1. 구글은 providerId만으로 연동 해제 불가
     * 2. 액세스 토큰 또는 리프레시 토큰이 필요
     * 3. 토큰을 별도 저장하거나 사용자가 직접 해제해야 함
     */
    private boolean unlinkGoogle(String providerId) {
        log.warn("구글 연동 해제는 현재 시뮬레이션 모드입니다.");
        log.info("구글 연동 해제 시뮬레이션 - providerId: {}", providerId);

        // TODO: 실제 구현 시 필요한 사항들
        /*
         * 구글 연동 해제를 위해서는:
         * 1. 로그인 시 리프레시 토큰을 DB에 저장
         * 2. 연동 해제 시 저장된 토큰으로 revoke API 호출
         * 3. 또는 사용자에게 구글 계정 설정에서 직접 해제하도록 안내
         *
         * API 엔드포인트: https://oauth2.googleapis.com/revoke
         * 필요 파라미터: token (access_token 또는 refresh_token)
         */

        log.info("구글 연동 해제를 위해서는 사용자가 직접 구글 계정 설정에서 해제해야 합니다.");
        log.info("구글 계정 설정: https://myaccount.google.com/permissions");

        // 시뮬레이션으로 성공 처리 (실제로는 사용자 안내만)
        return true;
    }

    /**
     * 네이버 연동 해제 - 시뮬레이션 (실제 구현 필요)
     *
     * 네이버 연동 해제 구현의 어려움:
     * 1. 네이버는 공식적인 연동 해제 API가 없음
     * 2. 토큰 삭제로만 처리 가능
     * 3. 사용자가 직접 네이버 계정에서 해제해야 함
     */
    private boolean unlinkNaver(String providerId) {
        log.warn("⚠네이버 연동 해제는 현재 시뮬레이션 모드입니다.");
        log.info("네이버 연동 해제 시뮬레이션 - providerId: {}", providerId);

        // TODO: 실제 구현 시 고려사항
        /*
         * 네이버 연동 해제 방법:
         * 1. 저장된 액세스 토큰 삭제 (서버에서)
         * 2. 사용자에게 네이버 계정에서 직접 해제하도록 안내
         *
         * 네이버는 공식 연동해제 API가 없어서 완전한 해제가 어려움
         * 대부분 토큰 무효화 + 사용자 직접 해제 방식 사용
         */

        log.info("💡 네이버 연동 해제를 위해서는 사용자가 직접 네이버 계정 설정에서 해제해야 합니다.");
        log.info("📎 네이버 계정 설정: https://nid.naver.com/user2/help/myInfo?lang=ko_KR");

        // 시뮬레이션으로 성공 처리 (실제로는 사용자 안내만)
        return true;
    }

    /**
     * 연동 해제 지원 여부 확인
     */
    public boolean isUnlinkSupported(String provider) {
        switch (provider.toUpperCase()) {
            case "KAKAO":
                return true;   // 완전 지원
            case "GOOGLE":
            case "NAVER":
                return false;  // 시뮬레이션만
            default:
                return false;
        }
    }

    /**
     * 연동 해제 지원 상태 메시지
     */
    public String getUnlinkSupportMessage(String provider) {
        switch (provider.toUpperCase()) {
            case "KAKAO":
                return "카카오 연동이 자동으로 해제됩니다.";
            case "GOOGLE":
                return "구글 계정 설정에서 직접 해제해주세요. (https://myaccount.google.com/permissions)";
            case "NAVER":
                return "네이버 계정 설정에서 직접 해제해주세요.";
            default:
                return "해당 제공자는 지원하지 않습니다.";
        }
    }
}
