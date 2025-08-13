package org.iebbuda.mozi.domain.profile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.dto.PersonalInfoStatusDTO;
import org.iebbuda.mozi.domain.profile.dto.UserProfileInfoDTO;
import org.iebbuda.mozi.domain.profile.mapper.UserProfileMapper;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.iebbuda.mozi.domain.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final UserMapper userMapper;


    // 저장 (검증 로직 제거)
    @Transactional
    @Override
    public void saveProfile(String loginId, UserProfileInfoDTO dto) {
        log.info("프로필 저장 시작: loginId={}", loginId);

        // 받아온 DTO 값 확인
        log.info("받아온 DTO: region={}, age={}, maritalStatus={}, annualIncome={}, educationLevel={}, employmentStatus={}, major={}, specialty={}",
                dto.getRegion(), dto.getAge(), dto.getMaritalStatus(),
                dto.getAnnualIncome(), dto.getEducationLevel(), dto.getEmploymentStatus(),
                dto.getMajor(), dto.getSpecialty());

        // 1. 사용자 검증
        UserVO user = findUserByLoginId(loginId);

        // 2. 기존 프로필 확인
        UserProfileVO currentProfile = userProfileMapper.findByUserId(user.getUserId());
        UserProfileVO profileToSave = dto.toVO(user.getUserId());

        // 변환된 VO 값 확인
        log.info("변환된 VO: region={}, age={}, maritalStatus={}, annualIncome={}, educationLevel={}, employmentStatus={}, major={}, specialty={}",
                profileToSave.getRegion(), profileToSave.getAge(), profileToSave.getMaritalStatus(),
                profileToSave.getAnnualIncome(), profileToSave.getEducationLevel(), profileToSave.getEmploymentStatus(),
                profileToSave.getMajor(), profileToSave.getSpecialty());

        // 3. 저장 또는 수정
        if (currentProfile != null) {
            updateProfile(loginId, profileToSave);
        } else {
            insertProfile(loginId, profileToSave);
        }

        log.info("프로필 저장 완료: loginId={}", loginId);
    }

    /**
     * 사용자 프로필 조회
     */
    @Override
    public UserProfileInfoDTO getUserProfile(String loginId) {
        log.info("프로필 조회 시작: loginId={}", loginId);

        // 1. 사용자 검증
        UserVO user = findUserByLoginId(loginId);

        // 2. 프로필 조회
        UserProfileVO userProfile = userProfileMapper.findByUserId(user.getUserId());
        UserProfileInfoDTO userProfileInfoDTO = UserProfileInfoDTO.of(userProfile);

        // 3. 빈 프로필인 경우 기본 객체 반환
        if (!hasProfileData(userProfileInfoDTO)) {
            log.debug("빈 프로필 반환: loginId={}", loginId);
            return UserProfileInfoDTO.builder().build();
        }

        log.info("프로필 조회 완료: loginId={}, hasData=true", loginId);
        return userProfileInfoDTO;
    }

    @Override
    public PersonalInfoStatusDTO getPersonalInfoStatus(String loginId) {
        UserVO user = findUserByLoginId(loginId);

        UserProfileVO profile = userProfileMapper.findByUserId(user.getUserId());
        boolean hasPersonalInfo = (profile != null);

        LocalDateTime createdAt = user.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();
        long daysPassed = ChronoUnit.DAYS.between(createdAt, now);
        int daysRemaining = Math.max(0, 14 - (int)daysPassed);

        // 4. 프롬프트 표시 필요 여부 판단
        boolean needsPrompt = !hasPersonalInfo && (daysPassed <= 14);

        return PersonalInfoStatusDTO.builder()
                .hasPersonalInfo(hasPersonalInfo)
                .createdAt(createdAt)
                .daysRemaining(daysRemaining)
                .needsPrompt(needsPrompt)
                .build();
    }

    @Override
    @Transactional
    public void deleteProfileByUserId(int userId) {
        log.info("사용자 프로필 데이터 삭제 시작 - userId: {}", userId);
        userProfileMapper.deleteProfileByUserId(userId);
        log.info("사용자 프로필 데이터 삭제 완료 - userId: {}", userId);
    }

    // ========== Private Methods ==========

    /**
     * 사용자 조회 및 검증
     */
    private UserVO findUserByLoginId(String loginId) {
        UserVO user = userMapper.findByLoginId(loginId);

        if (user == null) {
            log.error("사용자를 찾을 수 없습니다: loginId={}", loginId);
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER);
        }

        return user;
    }

    /**
     * 프로필 신규 저장
     */
    private void insertProfile(String loginId, UserProfileVO profile) {
        userProfileMapper.insertUserProfile(profile);
        log.info("사용자 {} 프로필 생성 완료", loginId);
    }

    /**
     * 프로필 업데이트
     */
    private void updateProfile(String loginId, UserProfileVO profile) {
        userProfileMapper.updateUserProfile(profile);
        log.info("사용자 {} 프로필 수정 완료", loginId);
    }

    /**
     * 프로필 데이터가 비어있는지 확인
     */
    private boolean hasProfileData(UserProfileInfoDTO dto) {
        if (dto == null) return false;

        return dto.getRegion() != null ||
                dto.getAge() != null ||
                dto.getMaritalStatus() != null ||
                dto.getAnnualIncome() != null ||
                dto.getEducationLevel() != null ||
                dto.getEmploymentStatus() != null ||
                dto.getMajor() != null ||
                dto.getSpecialty() != null;
    }
}

