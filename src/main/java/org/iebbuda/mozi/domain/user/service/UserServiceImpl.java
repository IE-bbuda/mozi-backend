package org.iebbuda.mozi.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.security.account.domain.AuthVO;
import org.iebbuda.mozi.domain.security.account.domain.UserRole;
import org.iebbuda.mozi.domain.user.domain.UserVO;


import org.iebbuda.mozi.domain.user.dto.request.EmailCodeVerifyRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.LoginIdFindResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.UserDTO;
import org.iebbuda.mozi.domain.user.dto.request.UserJoinRequestDTO;

import org.iebbuda.mozi.domain.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    //로그인Id 중복 체크
    @Override
    public boolean checkDuplicate(String loginId) {
        log.info("중복 확인 요청 - 로그인ID: {}", loginId);
        boolean available = mapper.findByLoginId(loginId) == null;
        log.info("중복 확인 결과 - 로그인ID: {}, 사용가능: {}", loginId, available);
        return available;
    }

    // 이메일 중복 체크 추가
    @Override
    public boolean checkEmailDuplicate(String email) {
        log.info("이메일 중복 확인 요청 - 이메일: {}", email);
        boolean available = mapper.findByEmail(email) == null;
        log.info("이메일 중복 확인 결과 - 이메일: {}, 사용가능: {}", email, available);
        return available;
    }

    /**
     * 회원가입용 이메일 인증번호 발송
     * @param email 인증번호를 받을 이메일 주소
     */
    @Override
    @Transactional
    public void sendSignupEmailVerification(String email) {
        log.info("회원가입 이메일 인증번호 발송 시작 - 이메일: {}", email);

        // 이메일 중복 체크 먼저 수행
        if (!checkEmailDuplicate(email)) {
            log.warn("중복된 이메일로 인증번호 발송 시도: {}", email);
            throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);
        }

        // 인증번호 발송
        emailVerificationService.sendSignupVerificationCode(email);

        log.info("회원가입 이메일 인증번호 발송 완료 - 이메일: {}", email);
    }

    /**
     * 회원가입용 이메일 인증번호 확인
     * @param request 이메일과 인증번호 정보
     */
    @Override
    @Transactional
    public void verifySignupEmailCode(EmailCodeVerifyRequestDTO request) {
        log.info("회원가입 이메일 인증번호 확인 시작 - 이메일: {}", request.getEmail());

        if (!emailVerificationService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            log.warn("회원가입 이메일 인증번호 확인 실패 - 이메일: {}", request.getEmail());
            throw new BaseException(BaseResponseStatus.INVALID_VERIFICATION_CODE);
        }

        log.info("회원가입 이메일 인증번호 확인 완료 - 이메일: {}", request.getEmail());
    }


    @Override
    public Optional<UserDTO> get(int userId) {
        UserVO user = mapper.findByUserId(userId);
        log.debug("사용자 조회 완료 - ID: {}, 사용자 존재 여부: {}", userId, user != null);
        return Optional.ofNullable(user).map(UserDTO::of);
    }

    @Transactional
    @Override
    public int join(UserJoinRequestDTO dto) {
        log.info("회원가입 시작 - 로그인 ID: {}", dto.getLoginId());

        // 로그인 ID 중복 체크
        if (!checkDuplicate(dto.getLoginId())) {
            log.warn("중복된 아이디로 가입 시도: {}", dto.getLoginId());
            throw new BaseException(BaseResponseStatus.DUPLICATE_LOGIN_ID);
        }

        // 이메일 중복 체크
        if (!checkEmailDuplicate(dto.getEmail())) {
            log.warn("중복된 이메일로 가입 시도: {}", dto.getEmail());
            throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);
        }

        // 이메일 인증 여부 체크
        if (!emailVerificationService.isEmailVerified(dto.getEmail())) {
            log.warn("이메일 인증 미완료로 가입 시도: {}", dto.getEmail());
            throw new BaseException(BaseResponseStatus.EMAIL_NOT_VERIFIED);
        }

        // 추가 검증: 이메일 인증 여부는 프론트엔드에서 체크 후 넘어온다고 가정

        UserVO user = dto.toVO(passwordEncoder);
        mapper.insert(user);

        AuthVO auth = new AuthVO(user.getUserId(), UserRole.ROLE_USER);
        mapper.insertAuth(auth);

        // 회원가입 완료 후 인증 상태 제거
        emailVerificationService.clearVerifiedStatus(dto.getEmail());

        log.info("회원가입 완료 - userId: {}, loginId: {}", user.getUserId(), dto.getLoginId());
        return user.getUserId();
    }


    @Override
    public LoginIdFindResponseDTO findLoginIdByEmail(String username, String email) {
        log.info("ID 찾기 요청 - username: {}, email: {}", username, email);

        Optional<String> loginId = Optional.ofNullable(mapper.findLoginIdByEmail(username, email));

        if (loginId.isPresent()) {
            log.info("ID 찾기 성공 - username: {}", username);
            return LoginIdFindResponseDTO.success(loginId.map(this::maskLoginId).get());
        } else {
            log.warn("ID 찾기 실패 - 일치하는 정보 없음, username: {}, email: {}", username, email);
            return LoginIdFindResponseDTO.notFound();
        }
    }

    private String maskLoginId(String loginId) {
        if (loginId.length() <= 2) {
            return "*".repeat(loginId.length());
        }
        if (loginId.length() <= 6) {
            return loginId.substring(0, 2) + "***";
        }
        // 긴 아이디는 앞 4글자 + *** + 끝 1글자
        return loginId.substring(0, 4) + "***" + loginId.charAt(loginId.length() - 1);
    }

}
