package org.iebbuda.mozi.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.mapper.UserProfileMapper;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.iebbuda.mozi.domain.user.dto.request.EmailCodeVerifyRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.MyPageUpdateRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.MyPageEditResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.MyPageResponseDTO;
import org.iebbuda.mozi.domain.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;


    /**
     * 마이페이지 수정 전 비밀번호 확인
     * @param loginId 로그인 ID
     * @param inputPassword 입력된 비밀번호
     * @throws BaseException 비밀번호가 일치하지 않을 경우
     */
    public void confirmPassword(String loginId, String inputPassword) {
        log.info("비밀번호 확인 요청 - 로그인ID: {}", loginId);

        UserVO user = findUserByLoginId(loginId);
        validateNotOAuthUser(user, "비밀번호 확인");
        if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
            log.warn("비밀번호 불일치 - 로그인ID: {}", loginId);
            throw new BaseException(BaseResponseStatus.INVALID_PASSWORD);
        }

        log.info("비밀번호 확인 성공 - 로그인ID: {}", loginId);
    }

    /**
     * 마이페이지 정보 조회 - 기본 정보 + 프로필 정보 통합
     */
    public MyPageResponseDTO getMyPageInfo(String loginId) {
        log.info("마이페이지 정보 조회 시작: loginId={}", loginId);

        // 1. 기본 사용자 정보 조회
        UserVO userVO = findUserByLoginId(loginId);

        // 2. 사용자 프로필 정보 조회
        UserProfileVO userProfileVO = getUserProfile(userVO.getUserId());

        log.info("마이페이지 정보 조회 완료: loginId={}, hasProfile={}",
                loginId, userProfileVO != null);

        return MyPageResponseDTO.of(userVO, userProfileVO);
    }

    /**
     * 마이페이지 수정 화면용 정보 조회 (기본 정보만)
     */
    public MyPageEditResponseDTO getMyPageEditInfo(String loginId) {
        log.info("마이페이지 수정 화면 정보 조회: loginId={}", loginId);

        UserVO user = findUserByLoginId(loginId);
        validateNotOAuthUser(user, "내 정보 수정 조회");

        log.info("마이페이지 수정 화면 정보 조회 완료: UserId={}", user.getUserId());
        return MyPageEditResponseDTO.of(user);
    }

    /**
     * 마이페이지 이메일 변경용 인증번호 발송
     */
    @Transactional
    public void sendMyPageEmailVerification(String loginId, String email) {
        log.info("마이페이지 이메일 인증번호 발송 시작 - loginId: {}, email: {}", loginId, email);

        UserVO user = findUserByLoginId(loginId);
        validateNotOAuthUser(user, "이메일 확인");
        // 이메일 변경 유효성 검사
        validateEmailChange(loginId, email);

        // 마이페이지용 인증번호 발송
        emailVerificationService.sendMyPageVerificationCode(email);

        log.info("마이페이지 이메일 인증번호 발송 완료 - email: {}", email);
    }

    /**
     * 마이페이지 이메일 변경용 인증번호 확인
     */
    @Transactional
    public void verifyMyPageEmailCode(String loginId, EmailCodeVerifyRequestDTO request) {
        log.info("마이페이지 이메일 인증번호 확인 시작 - loginId: {}, email: {}", loginId, request.getEmail());

        if (!emailVerificationService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            log.warn("마이페이지 이메일 인증번호 확인 실패 - email: {}", request.getEmail());
            throw new BaseException(BaseResponseStatus.INVALID_VERIFICATION_CODE);
        }

        log.info("마이페이지 이메일 인증번호 확인 완료 - email: {}", request.getEmail());
    }


    /**
     * 마이페이지 기본 정보 수정
     */
    @Transactional
    public void updateMyPageInfo(String loginId, MyPageUpdateRequestDTO request) {
        log.info("마이페이지 정보 수정 시작: loginId={}", loginId);

        // 1. 사용자 존재 여부 확인
        UserVO user = findUserByLoginId(loginId);
        validateNotOAuthUser(user, "내 정보 수정 실행");
// 2. 변경 사항 확인
        boolean isEmailChanged = isEmailChanged(request, user);
        boolean isPasswordChanged = request.getPassword() != null && !request.getPassword().trim().isEmpty();

        log.info("변경 사항 - 이메일: {}, 비밀번호: {}", isEmailChanged, isPasswordChanged);

        // 3. 이메일 변경 시 인증 확인
        verifyEmailIfChanged(request, isEmailChanged);

        // 4. 실제 변경이 있는 경우에만 업데이트 수행
        if (isPasswordChanged || isEmailChanged) {

            // 비밀번호 변경
            if (isPasswordChanged) {
                String encodedPassword = passwordEncoder.encode(request.getPassword());
                userMapper.updatePasswordByLoginId(user.getLoginId(), encodedPassword);
                log.info("비밀번호 업데이트 완료 - loginId: {}", loginId);
            }

            // 이메일 변경
            if (isEmailChanged) {
                userMapper.updateUserInfo(user.getLoginId(), request.getEmail());
                log.info("이메일 업데이트 완료 - loginId: {}, 새 이메일: {}", loginId, request.getEmail());

                // 이메일 변경된 경우 인증 상태 제거
                emailVerificationService.clearVerifiedStatus(request.getEmail());
                log.info("이메일 인증 상태 제거 완료");
            }

        } else {
            log.warn("실제 변경 사항이 없음 - loginId: {}", loginId);
        }

        log.info("마이페이지 정보 수정 완료: loginId={}", loginId);
    }



    // ========== Private Methods ==========

    /**
     * 사용자 기본 정보 조회 및 검증
     */
    private UserVO findUserByLoginId(String loginId) {
        UserVO userVO = userMapper.findByLoginId(loginId);

        if (userVO == null) {
            log.error("사용자를 찾을 수 없습니다: loginId={}", loginId);
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER);
        }

        return userVO;
    }

    /**
     * 사용자 프로필 정보 조회 (private 메서드)
     */
    private UserProfileVO getUserProfile(int userId) {
            UserProfileVO userProfileVO = userProfileMapper.findByUserId(userId);
            log.debug("프로필 정보 조회 결과: userId={}, hasProfile={}",
                    userId, userProfileVO != null);
            return userProfileVO;
    }

    /**
     * 이메일 변경 유효성 검사
     */
    private void validateEmailChange(String loginId, String newEmail) {
        UserVO currentUser = findUserByLoginId(loginId);

        // 현재 이메일과 동일한지 확인
        if (newEmail.equals(currentUser.getEmail())) {
            log.warn("현재 이메일과 동일한 이메일로 변경 시도 - email: {}", newEmail);
            throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);
        }

        // 다른 사용자가 이미 사용 중인 이메일인지 확인
        UserVO existingUser = userMapper.findByEmail(newEmail);
        if (existingUser != null) {
            log.warn("이미 사용 중인 이메일로 변경 시도 - email: {}", newEmail);
            throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);
        }
    }


    /**
     * 이메일 변경 여부 확인
     */
    private boolean isEmailChanged(MyPageUpdateRequestDTO request, UserVO user) {
        return  request.getEmail() != null && !request.getEmail().equals(user.getEmail());
    }

    /**
     * 이메일 변경 시 인증 확인
     */

    private void verifyEmailIfChanged(MyPageUpdateRequestDTO request, boolean isEmailChanged) {
        if (isEmailChanged) {
            if (!emailVerificationService.isEmailVerified(request.getEmail())) {
                log.warn("이메일 인증 미완료로 수정 시도 - email: {}", request.getEmail());
                throw new BaseException(BaseResponseStatus.EMAIL_NOT_VERIFIED);
            }
        }
    }

    /**
     * 이메일이 변경된 경우 인증 상태 제거
     */
    private void handleEmailVerificationReset(MyPageUpdateRequestDTO request, boolean isEmailChanged, UserVO user) {
        if (isEmailChanged) {
            emailVerificationService.clearVerifiedStatus(request.getEmail());
            log.info("이메일 변경 완료 및 인증 상태 제거 - 기존: {}, 신규: {}", user.getEmail(), request.getEmail());
        }
    }

    /**
     * OAuth 로그인은 차단
     */
    private void validateNotOAuthUser(UserVO user, String action) {
        if (user.getProvider() != null) {
            log.warn("OAuth 사용자의 {} 시도 차단 - provider: {}", action, user.getProvider());
            throw new BaseException(BaseResponseStatus.OAUTH_USER_ACCESS_DENIED);
        }
    }

}
