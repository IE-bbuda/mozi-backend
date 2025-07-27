package org.iebbuda.mozi.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
     * 마이페이지 정보 조회 - 기본 정보 + 프로필 정보 통합
     */
    public MyPageResponseDTO getMyPageInfo(String loginId) {
        log.info("마이페이지 정보 조회 시작: loginId={}", loginId);

        // 1. 기본 사용자 정보 조회
        UserVO userVO = userMapper.findByLoginId(loginId);
        if (userVO == null) {
            log.error("사용자를 찾을 수 없습니다: loginId={}", loginId);
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 2. 사용자 프로필 정보 조회
        UserProfileVO userProfileVO = getUserProfile(userVO.getUserId());

        log.info("마이페이지 정보 조회 완료: loginId={}, hasProfile={}",
                loginId, userProfileVO != null);

        return MyPageResponseDTO.of(userVO, userProfileVO);
    }

    /**
     * 마이페이지 기본 정보 수정
     */
    @Transactional
    public MyPageResponseDTO updateMyPageInfo(String loginId, MyPageUpdateRequestDTO request) {
        log.info("마이페이지 정보 수정 시작: loginId={}", loginId);

        // 1. 사용자 존재 여부 확인
        UserVO userVO = getUserVO(loginId);

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
     * 사용자 기본 정보 조회 (공통 메서드)
     */
    private UserVO getUserVO(String loginId) {
        UserVO userVO = userMapper.findByLoginId(loginId);
        if (userVO == null) {
            log.error("사용자를 찾을 수 없습니다: loginId={}", loginId);
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        return userVO;
    }

    /**
     * 마이페이지 수정 화면용 정보 조회 (기본 정보만)
     */
    public MyPageEditResponseDTO getMyPageEditInfo(String loginId) {
        log.info("마이페이지 수정 화면 정보 조회: loginId={}", loginId);

        UserVO userVO = getUserVO(loginId);

        log.info("마이페이지 수정 화면 정보 조회 완료: loginId={}", loginId);
        return MyPageEditResponseDTO.of(userVO);
    }

    /**
     * 사용자 프로필 정보 조회 (private 메서드)
     */
    private UserProfileVO getUserProfile(int userId) {
        try {
            UserProfileVO userProfileVO = userProfileMapper.findByUserId(userId);
            log.debug("프로필 정보 조회 결과: userId={}, hasProfile={}",
                    userId, userProfileVO != null);
            return userProfileVO;
        } catch (Exception e) {
            log.warn("프로필 정보 조회 실패: userId={}, error={}", userId, e.getMessage());
            return null;
        }
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

            int result = userMapper.updateUserInfo(loginId, finalEmail, finalPhone);

            if (result == 0) {
                log.error("기본 정보 수정 실패: loginId={}", loginId);
                throw new RuntimeException("정보 수정에 실패했습니다.");
            }

            log.info("기본 정보 수정 완료: loginId={}, emailChanged={}, phoneChanged={}",
                    loginId, emailToUpdate != null, phoneToUpdate != null);
        }
    }

    /**
     * 비밀번호 수정 처리
     */
    private void updatePasswordIfProvided(String loginId, MyPageUpdateRequestDTO request) {
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return;
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword().trim());
        int result = userMapper.updatePasswordByLoginId(loginId, encodedPassword);

        if (result == 0) {
            log.error("비밀번호 변경 실패: loginId={}", loginId);
            throw new RuntimeException("비밀번호 변경에 실패했습니다.");
        }

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
