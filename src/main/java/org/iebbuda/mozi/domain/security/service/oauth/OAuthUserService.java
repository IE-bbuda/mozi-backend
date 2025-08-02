package org.iebbuda.mozi.domain.security.service.oauth;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.security.account.domain.AuthVO;
import org.iebbuda.mozi.domain.security.account.domain.UserRole;
import org.iebbuda.mozi.domain.security.dto.oauth.OAuthProvider;
import org.iebbuda.mozi.domain.security.dto.oauth.OAuthUserInfo;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.iebbuda.mozi.domain.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class OAuthUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public UserVO processOAuthUser(OAuthUserInfo oAuthUserInfo) {
        String provider = oAuthUserInfo.getProvider().getCode();
        String providerId = oAuthUserInfo.getProviderId();

        // 1. 기존 OAuth 사용자 조회
        UserVO existingUser = userMapper.findByProviderAndProviderId(provider, providerId);
        if (existingUser != null) {
            log.info("기존 {} 사용자 로그인 - providerId: {}", provider, providerId);
            return existingUser;
        }

        // 2. 새로운 OAuth 사용자 생성
        return createOAuthUser(oAuthUserInfo);
    }
    private UserVO createOAuthUser(OAuthUserInfo oAuthUserInfo) {
        UserVO newUser = new UserVO();

        OAuthProvider provider = oAuthUserInfo.getProvider();
        String providerId = oAuthUserInfo.getProviderId();
        String nickname = extractNickname(oAuthUserInfo);
        String email = determineEmail(oAuthUserInfo, provider, providerId);

        // 고유한 loginId 생성 (provider_providerId)
        String loginId = provider.getCode().toLowerCase() + "_" + providerId;

        newUser.setLoginId(loginId);
        newUser.setUsername(nickname);
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setProvider(provider.getCode());
        newUser.setProviderId(providerId);
        newUser.setEmail(email);

        // 나머지 필수 필드들은 랜덤값으로 채우기
        newUser.setPhoneNumber(generateRandomPhoneNumber());
        newUser.setBirthDate(generateRandomBirthDate());

        log.info("{} 사용자 생성 - loginId: {}, username: {}, email: {}",
                provider.getDisplayName(), loginId, newUser.getUsername(), newUser.getEmail());

        // DB에 사용자 저장
        userMapper.insert(newUser);

        // 기본 권한 부여
        AuthVO auth = new AuthVO(newUser.getUserId(), UserRole.ROLE_USER);
        userMapper.insertAuth(auth);
        // 완전한 사용자 정보 반환
        return userMapper.findByLoginId(loginId);
    }

    // OAuth 닉네임 추출 메서드
    private String extractNickname(OAuthUserInfo oAuthUserInfo) {
        String nickname = oAuthUserInfo.getNickname();
        if (nickname != null && !nickname.isEmpty()) {
            return nickname;
        }

        String providerName = oAuthUserInfo.getProvider().getDisplayName();
        return providerName + "사용자" + generateRandomNumber(4);
    }

    // 이메일 결정 메서드 - 실제 이메일 우선, 중복되면 에러
    private String determineEmail(OAuthUserInfo oauthUserInfo, OAuthProvider provider, String providerId) {
        String realEmail = oauthUserInfo.getEmail();

        // 실제 이메일이 있으면 사용
        if (realEmail != null && !realEmail.trim().isEmpty()) {
            // 중복 체크 - 중복되면 로그인 차단
            if (isEmailAlreadyUsed(realEmail)) {
                log.error("{} 로그인 차단 - 이미 사용 중인 이메일: {}", provider.getDisplayName(), realEmail);
                throw new BaseException(BaseResponseStatus.OAUTH_EMAIL_ALREADY_EXISTS);
            }

            log.info("{} 실제 이메일 사용: {}", provider.getDisplayName(), realEmail);
            return realEmail;
        }

        // 이메일이 없으면 더미 이메일 생성
        String dummyEmail = generateOAuthEmail(provider, providerId);
        log.info("{} 더미 이메일 생성 (실제 이메일 없음): {}", provider.getDisplayName(), dummyEmail);
        return dummyEmail;
    }

    // 이메일 중복 체크
    private boolean isEmailAlreadyUsed(String email) {
        return userMapper.findByEmail(email) != null;
    }

    // OAuth 전용 더미 이메일 생성
    private String generateOAuthEmail(OAuthProvider provider, String providerId) {
        return provider.getCode().toLowerCase() + "_" + providerId + "@dummy.local";
    }

    // 랜덤 전화번호 생성 (010-XXXX-XXXX 형식)
    private String generateRandomPhoneNumber() {
        int middle = 1000 + random.nextInt(9000);
        int last = 1000 + random.nextInt(9000);
        return String.format("010-%04d-%04d", middle, last);
    }

    // 랜덤 생년월일 생성 (YYMMDD 형식, 1980~2005년 사이)
    private String generateRandomBirthDate() {
        int year = 80 + random.nextInt(26); // 80~05가 아니라 80~105가 됨!

        // 올바른 방법: 80~99, 00~05 분리
        int randomYear;
        if (random.nextBoolean()) {
            randomYear = 80 + random.nextInt(20); // 80~99 (1980~1999)
        } else {
            randomYear = random.nextInt(6); // 00~05 (2000~2005)
        }

        int month = 1 + random.nextInt(12); // 1~12
        int day = 1 + random.nextInt(28);   // 1~28

        return String.format("%02d%02d%02d", randomYear, month, day);
    }
        // 랜덤 숫자 생성
    private String generateRandomNumber(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
