package org.iebbuda.mozi.domain.security.filter;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.security.dto.LoginDTO;
import org.iebbuda.mozi.domain.security.handler.LoginFailureHandler;
import org.iebbuda.mozi.domain.security.handler.LoginSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Log4j2
@Component
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private JwtUsernamePasswordAuthenticationFilter(
            AuthenticationManager authenticationManager,
            LoginSuccessHandler loginSuccessHandler,
            LoginFailureHandler loginFailureHandler) {
        super(authenticationManager);

        setFilterProcessesUrl("/api/auth/login");
        setAuthenticationSuccessHandler(loginSuccessHandler);
        setAuthenticationFailureHandler(loginFailureHandler);
    }


    // 로그인 요청 URL인 경우 로그인 작업 처리
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 요청 BODY의 JSON에서 username, password  LoginDTO
        LoginDTO login = LoginDTO.of(request);

        // 인증 토큰(UsernamePasswordAuthenticationToken) 구성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login.getLoginId(), login.getPassword());

        return getAuthenticationManager().authenticate(authenticationToken);
    }
}

