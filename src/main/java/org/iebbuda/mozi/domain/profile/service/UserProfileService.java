package org.iebbuda.mozi.domain.profile.service;

import org.iebbuda.mozi.domain.profile.dto.PersonalInfoStatusDTO;
import org.iebbuda.mozi.domain.profile.dto.UserProfileInfoDTO;

import java.util.Map;

public interface UserProfileService {
    void saveProfile(String loginId, UserProfileInfoDTO dto);
    UserProfileInfoDTO getUserProfile(String userId);
    PersonalInfoStatusDTO getPersonalInfoStatus(String loginId);
    void deleteProfileByUserId(int userId);
}
