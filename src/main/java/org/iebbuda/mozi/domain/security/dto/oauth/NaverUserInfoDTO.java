package org.iebbuda.mozi.domain.security.dto.oauth;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NaverUserInfoDTO implements OAuthUserInfo {
    private String resultCode;
    private String message;

    @JsonProperty("response")
    private NaverResponse response;


    @Data
    public static class NaverResponse {
        private String id;
        private String nickname;
        private String email;
        private String name;

        @JsonProperty("profile_image")
        private String profileImage;

        private String gender;
        private String age;
        private String birthday;
        private String birthyear;
        private String mobile;
    }

    @Override
    public String getProviderId() {
        return response != null ? response.getId() : null;
    }

    @Override
    public String getNickname() {
        if (response != null) {
            // 네이버는 이름(name)을 주로 사용, 닉네임이 없으면 이름 사용
            return response.getName() != null ? response.getName() : response.getNickname();
        }
        return null;
    }

    @Override
    public String getEmail() {
        return response != null ? response.getEmail() : null;
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.NAVER;
    }
}
