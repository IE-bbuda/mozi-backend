package org.iebbuda.mozi.domain.security.service.oauth;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.security.dto.AuthResultDTO;
import org.iebbuda.mozi.domain.security.dto.UserInfoDTO;
import org.iebbuda.mozi.domain.security.dto.oauth.OAuthUserInfo;
import org.iebbuda.mozi.domain.security.util.JwtProcessor;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class OAuthLoginService {

    private final OAuthServiceFactory oAuthServiceFactory;
    private final OAuthUserService oAuthUserService;
    private final JwtProcessor jwtProcessor;

   public AuthResultDTO processOAuthLogin(String provider, String code){
       log.info("{} OAuth 로그인 처리 시작 - code: {}", provider, code);

       // 1. 적절한 OAuth 서비스 선택
       OAuthService oauthService = oAuthServiceFactory.getOAuthService(provider);

       // 2. OAuth 제공자에서 액세스 토큰 받기
       String accessToken = oauthService.getAccessToken(code);

       // 3. 액세스 토큰으로 사용자 정보 받기
       OAuthUserInfo oAuthUserInfo = oauthService.getUserInfo(accessToken);

       // 4. 사용자 정보로 회원가입/로그인 처리
       UserVO user = oAuthUserService.processOAuthUser(oAuthUserInfo);

       // 5. JWT 토큰 생성
       String jwtToken = jwtProcessor.generateToken(user.getLoginId());

       // 6. 응답 생성
       log.info("{} OAuth 로그인 처리 완료 - userId: {}", provider, user.getUserId());
       return new AuthResultDTO(jwtToken, UserInfoDTO.of(user));
   }
}
