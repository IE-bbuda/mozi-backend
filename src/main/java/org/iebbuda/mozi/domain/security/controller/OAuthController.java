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

//    @GetMapping("/google/callback")
//    public BaseResponse<AuthResultDTO> googleCallback(@RequestParam("code") String code) {
//        AuthResultDTO result = oauthLoginService.processOAuthLogin("GOOGLE", code);
//        return new BaseResponse<>(result);
//    }
//
//    @GetMapping("/naver/callback")
//    public BaseResponse<AuthResultDTO> naverCallback(@RequestParam("code") String code) {
//        AuthResultDTO result = oauthLoginService.processOAuthLogin("NAVER", code);
//        return new BaseResponse<>(result);
//    }

//    // 범용 OAuth 콜백 엔드포인트
//    @GetMapping("/{provider}/callback")
//    public BaseResponse<AuthResultDTO> oauthCallback(
//            @PathVariable("provider") String provider,
//            @RequestParam("code") String code) {
//        AuthResultDTO result = oauthLoginService.processOAuthLogin(provider.toUpperCase(), code);
//        return new BaseResponse<>(result);
//    }
}
