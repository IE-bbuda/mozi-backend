package org.iebbuda.mozi.domain.security.dto.oauth;

public enum OAuthProvider {
    KAKAO("KAKAO", "카카오"),
    GOOGLE("GOOGLE", "구글"),
    NAVER("NAVER", "네이버");

    private final String code;
    private final String displayName;

    OAuthProvider(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
