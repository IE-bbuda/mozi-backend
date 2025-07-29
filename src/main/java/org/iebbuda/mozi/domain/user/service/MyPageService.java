package org.iebbuda.mozi.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.mapper.UserProfileMapper;
import org.iebbuda.mozi.domain.user.domain.UserVO;
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


    /**
     * 마이페이지 수정 전 비밀번호 확인
     * @param loginId 로그인 ID
     * @param inputPassword 입력된 비밀번호
     * @throws BaseException 비밀번호가 일치하지 않을 경우
     */
    public void confirmPassword(String loginId, String inputPassword) {
        log.info("비밀번호 확인 요청 - 로그인ID: {}", loginId);

        UserVO user = userMapper.findByLoginId(loginId);
        if (user == null) {
            log.warn("사용자를 찾을 수 없음 - 로그인ID: {}", loginId);
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER);
        }

        boolean matches = passwordEncoder.matches(inputPassword, user.getPassword());
        if (!matches) {
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

        UserVO userVO = findUserByLoginId(loginId);

        log.info("마이페이지 수정 화면 정보 조회 완료: loginId={}", loginId);
        return MyPageEditResponseDTO.of(userVO);
    }

    /**
     * 마이페이지 기본 정보 수정
     */
    @Transactional
    public MyPageResponseDTO updateMyPageInfo(String loginId, MyPageUpdateRequestDTO request) {
        log.info("마이페이지 정보 수정 시작: loginId={}", loginId);

        // 1. 사용자 존재 여부 확인
        UserVO userVO = findUserByLoginId(loginId);

        // 2. 기본 정보 수정 처리
        updateBasicInfoIfChanged(loginId, userVO, request);

        // 3. 비밀번호 수정 처리
        updatePasswordIfProvided(loginId, request);

        // 4. 수정 후 최신 정보 반환
        log.info("마이페이지 정보 수정 완료: loginId={}", loginId);
        return getMyPageInfo(loginId);
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
     * 기본 정보 수정 처리 (이메일, 전화번호)
     */
    private void updateBasicInfoIfChanged(String loginId, UserVO userVO, MyPageUpdateRequestDTO request) {
        String emailToUpdate = prepareEmailUpdate(userVO.getEmail(), request.getEmail());
        String phoneToUpdate = preparePhoneUpdate(userVO.getPhoneNumber(), request.getPhoneNumber());

        // 변경사항이 있을 때만 업데이트
        if (emailToUpdate != null || phoneToUpdate != null) {
            String finalEmail = getFinalValue(emailToUpdate, userVO.getEmail());
            String finalPhone = getFinalValue(phoneToUpdate, userVO.getPhoneNumber());

            // DB 업데이트 수행 - DB 예외는 자동으로 전파됨
            userMapper.updateUserInfo(loginId, finalEmail, finalPhone);

            log.info("기본 정보 수정 완료: loginId={}, emailChanged={}, phoneChanged={}",
                    loginId, emailToUpdate != null, phoneToUpdate != null);
        } else {
            log.debug("기본 정보 변경사항 없음: loginId={}", loginId);
        }
    }

    /**
     * 비밀번호 수정 처리
     */
    private void updatePasswordIfProvided(String loginId, MyPageUpdateRequestDTO request) {
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.debug("비밀번호 변경 요청 없음: loginId={}", loginId);
            return;
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword().trim());
        int result = userMapper.updatePasswordByLoginId(loginId, encodedPassword);

        log.info("비밀번호 변경 완료: loginId={}", loginId);
    }

    /**
     * 이메일 변경사항 확인 및 준비
     */
    private String prepareEmailUpdate(String currentEmail, String newEmail) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            return null;
        }

        String trimmedEmail = newEmail.trim();
        if (trimmedEmail.equals(currentEmail)) {
            log.debug("이메일 변경사항 없음: {}", currentEmail);
            return null;
        }

        log.info("이메일 수정 예정: {} -> {}", currentEmail, trimmedEmail);
        return trimmedEmail;
    }

    /**
     * 전화번호 변경사항 확인 및 준비
     */
    private String preparePhoneUpdate(String currentPhone, String newPhone) {
        if (newPhone == null || newPhone.trim().isEmpty()) {
            return null;
        }

        String trimmedPhone = newPhone.trim();
        if (trimmedPhone.equals(currentPhone)) {
            log.debug("전화번호 변경사항 없음: {}", currentPhone);
            return null;
        }

        log.info("전화번호 수정 예정: {} -> {}", currentPhone, trimmedPhone);
        return trimmedPhone;
    }

    /**
     * 최종 값 결정 (null이면 기본값 사용)
     */
    private String getFinalValue(String newValue, String defaultValue) {
        if (newValue != null) {
            return newValue;
        }
        return defaultValue;
    }
}
