package org.iebbuda.mozi.domain.security.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.domain.security.dto.AuthResultDTO;
import org.iebbuda.mozi.domain.security.service.oauth.OAuthLoginService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthLoginService oauthLoginService;


    @GetMapping("/kakao/login-url")
    public BaseResponse<String> getKakaoLoginUrl() {
        String loginUrl = oauthLoginService.getLoginUrl("KAKAO");
        return new BaseResponse<>(loginUrl);
    }

    @GetMapping("/kakao/callback")
    public BaseResponse<AuthResultDTO> kakaoCallback(@RequestParam("code") String code) {
        AuthResultDTO result = oauthLoginService.processOAuthLogin("KAKAO", code);
        return new BaseResponse<>(result);
    }

    // ===== 네이버 =====
    @GetMapping("/naver/login-url")
    public BaseResponse<String> getNaverLoginUrl() {
        String loginUrl = oauthLoginService.getLoginUrl("NAVER");
        return new BaseResponse<>(loginUrl);
    }

    @GetMapping("/naver/callback")
    public BaseResponse<AuthResultDTO> naverCallback(@RequestParam("code") String code) {
        AuthResultDTO result = oauthLoginService.processOAuthLogin("NAVER", code);
        return new BaseResponse<>(result);
    }
    // ===== 구글 =====
    @GetMapping("/google/login-url")
    public BaseResponse<String> getGoogleLoginUrl() {
        String loginUrl = oauthLoginService.getLoginUrl("GOOGLE");
        return new BaseResponse<>(loginUrl);
    }

    @GetMapping("/google/callback")
    public BaseResponse<AuthResultDTO> googleCallback(@RequestParam("code") String code) {
        AuthResultDTO result = oauthLoginService.processOAuthLogin("GOOGLE", code);
        return new BaseResponse<>(result);
    }
}
