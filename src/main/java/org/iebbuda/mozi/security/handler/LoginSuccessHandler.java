package org.iebbuda.mozi.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.security.account.domain.CustomUser;
import org.iebbuda.mozi.security.dto.AuthResultDTO;
import org.iebbuda.mozi.security.dto.UserInfoDTO;
import org.iebbuda.mozi.security.util.JsonResponse;
import org.iebbuda.mozi.security.util.JwtProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProcessor jwtProcessor;

    private AuthResultDTO makeAuthResult(CustomUser user){
        String loginId = user.getUsername();
        String token = jwtProcessor.generateToken(loginId);
        return new AuthResultDTO(token, UserInfoDTO.of(user.getUser()));
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUser user = (CustomUser) authentication.getPrincipal();


        AuthResultDTO result = makeAuthResult(user);
        JsonResponse.send(response, result);
    }
}
