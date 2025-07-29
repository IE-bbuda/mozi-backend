package org.iebbuda.mozi.domain.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@PropertySource({"classpath:/application.properties"})
public class JwtProcessor {
    static private final long TOKEN_VALID_MILISECOND = 1000L * 60 * 5; //5분


    //개발시 사용
    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 운영시 사용
    // private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //JWT 생성
    public String generateToken(String subject){
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + TOKEN_VALID_MILISECOND))
                .signWith(key)
                .compact();
    }

    //JWT Subject(loginId) 추출 - 해석 불가인 경우 예외 발생
   public String getLoginId(String token){
       return Jwts.parserBuilder()
               .setSigningKey(key)
               .build()
               .parseClaimsJws(token)
               .getBody()
               .getSubject();
   }

    // JWT 검증(유효 기간 검증) - 해석 불가인 경우 예외 발생
    public boolean validateToken(String token){
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return true;
    }
}
