package org.iebbuda.mozi.domain.security.config;



import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.security.filter.AuthenticationErrorFilter;
import org.iebbuda.mozi.domain.security.filter.JwtAuthenticationFilter;
import org.iebbuda.mozi.domain.security.filter.JwtUsernamePasswordAuthenticationFilter;
import org.iebbuda.mozi.domain.security.handler.CustomAccessDeniedHandler;
import org.iebbuda.mozi.domain.security.handler.CustomAuthenticationEntryPoint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableWebSecurity
@Log4j2
@MapperScan(basePackages = {"org.iebbuda.mozi.domain.security.account.mapper"})
@ComponentScan(basePackages = {"org.iebbuda.mozi.domain.security"})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationErrorFilter authenticationErrorFilter;

    @Autowired
    private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //문자셋 필터
    public CharacterEncodingFilter encodingFilter() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return encodingFilter;
    }

    // AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    // cross origin 접근 허용
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    // 접근 제한 무시 경로 설정 – resource
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(request ->
                !request.getRequestURI().startsWith("/api/")  // API가 아닌 모든 요청 허용
        );
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 한글 인코딩 필터 설정
        http.addFilterBefore(encodingFilter(), CsrfFilter.class)
                .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http
                .authorizeRequests()
                // CORS preflight 요청 허용
                .antMatchers(HttpMethod.OPTIONS).permitAll()

                // 인증 없이 접근 가능한 API들
                .antMatchers("/api/auth/login").permitAll()                    // 로그인
                .antMatchers("/api/users/signup").permitAll()                  // 회원가입
                .antMatchers("/api/users/check-username/**").permitAll()       // 아이디 중복 확인
                .antMatchers("/api/users/find-id").permitAll()                 // 아이디 찾기
                .antMatchers("/api/users/signup/send-email-code").permitAll()  // 회원가입 이메일 인증
                .antMatchers("/api/users/signup/verify-email-code").permitAll() // 회원가입 이메일 인증 확인
                .antMatchers("/api/users/password/send-email-code").permitAll() // 비밀번호 재설정 이메일 인증
                .antMatchers("/api/users/password/verify-email-code").permitAll() // 비밀번호 재설정 이메일 인증 확인
                .antMatchers("/api/users/password/verify-account").permitAll()  // 계정 확인
                .antMatchers("/api/users/password/reset").permitAll()          // 비밀번호 재설정
                .antMatchers("/api/deposits").permitAll()
                .antMatchers("/api/deposits/{id}").permitAll()
                .antMatchers("/api/deposits/top").permitAll()
                .antMatchers("/api/savings").permitAll()
                .antMatchers("/api/savings/{id}").permitAll()
                .antMatchers("/api/savings/top").permitAll()
                .antMatchers("/api/policy").permitAll()
                .antMatchers("/api/policy/{id}").permitAll()
                .antMatchers("/api/policy/filter").permitAll()
                .antMatchers("/api/policy/deadline").permitAll()
                .antMatchers("/api/region").permitAll()
                .antMatchers("/api/region/zipcodes").permitAll()
                .antMatchers("/api/region/names").permitAll()
                .antMatchers("/api/region/zipcodes/sido").permitAll()


                // OAuth 관련 API들
                .antMatchers("/api/oauth/**").permitAll()                      // OAuth 로그인 (카카오, 구글, 네이버 등)

                // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated();
        http.httpBasic().disable() // 기본 HTTP 인증 비활성화
                .csrf().disable() // CSRF 비활성화
                .formLogin().disable() // formLogin 비활성화  관련 필터 해제
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션 생성 모드 설정
    }

    // Authentication Manger 구성
    // 사용자 정보를 어디서 얻을지 설정
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

}

