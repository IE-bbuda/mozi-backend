package org.iebbuda.mozi.profile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.profile.domain.UserProfileVO;
import org.iebbuda.mozi.profile.dto.UserProfileInfoDTO;
import org.iebbuda.mozi.profile.mapper.UserProfileMapper;
import org.iebbuda.mozi.user.domain.UserVO;
import org.iebbuda.mozi.user.mapper.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final UserMapper userMapper;


    // 저장 (검증 로직 제거)
    @Transactional
    public Map<String, Object> saveProfile(String loginId, UserProfileInfoDTO dto) {
        try {
            // 기존 데이터 확인
            UserVO user = userMapper.findByLoginId(loginId);
            UserProfileVO  currentProfile= userProfileMapper.findByUserId(user.getUserId());
            UserProfileVO  insertedProfile = dto.toVO(user.getUserId());
            if (currentProfile != null) {
                // 수정
                int result = userProfileMapper.updateUserProfile(insertedProfile);
                if(result==0){
                    return Map.of("success", false, "message", "수정에 실패했습니다.");
                }
                log.info("사용자 {} 프로필 수정 완료", loginId);

                return Map.of(
                        "success", true,
                        "message", "프로필이 수정되었습니다."
                );
            } else {
                int result = userProfileMapper.insertUserProfile(insertedProfile);
                if(result ==0){
                    return Map.of("success", false, "message", "저장에 실패했습니다.");
                }

                log.info("사용자 {} 프로필 생성 완료", loginId);
                return Map.of(
                        "success", true,
                        "message", "프로필이 저장되었습니다."
                );
            }

        } catch (Exception e) {
            log.error("설문 저장 실패: userId={}", loginId, e);
            return Map.of(
                    "success", false,
                    "message", "저장에 실패했습니다."
            );
        }
    }

    @Override
    public Map<String, Object> getUserProfile(String loginId) {
        try {
            UserVO user = userMapper.findByLoginId(loginId);
            UserProfileVO userProfile = userProfileMapper.findByUserId(user.getUserId());
            UserProfileInfoDTO userProfileInfoDTO = UserProfileInfoDTO.of(userProfile);
            boolean hasExistingData = isNotEmpty(userProfileInfoDTO);

            return Map.of(
                    "success", true,
                    "data", getProfileDTO(hasExistingData, userProfileInfoDTO),
                    "hasExistingData", hasExistingData
            );
        } catch (Exception e) {
            log.error("프로필 조회 실패: userId={}", loginId, e);
            throw new RuntimeException("프로필 조회에 실패했습니다.", e);
        }
    }


    private Object getProfileDTO(boolean hasExistingData, UserProfileInfoDTO userProfileInfoDTO) {
        if(hasExistingData) return userProfileInfoDTO;
        return new UserProfileInfoDTO(); // 빈 객체 반환
    }

    private boolean isNotEmpty(UserProfileInfoDTO dto) {
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

