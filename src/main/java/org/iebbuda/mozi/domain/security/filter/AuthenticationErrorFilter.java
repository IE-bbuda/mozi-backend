package org.iebbuda.mozi.domain.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.iebbuda.mozi.domain.security.util.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.MalformedInputException;


@Component
public class AuthenticationErrorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            super.doFilter(request,response,filterChain);
        }catch (ExpiredJwtException e){
            JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED,"토큰의 유효시간이 지났습니다.");
        }catch (UnsupportedEncodingException | MalformedInputException | SignatureException e){
            JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }catch (ServletException e){
            JsonResponse.sendError(response,HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
}
