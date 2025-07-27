package org.iebbuda.mozi.domain.profile.service;

import org.iebbuda.mozi.domain.profile.dto.UserProfileInfoDTO;

import java.util.Map;

public interface UserProfileService {
    Map<String, Object> saveProfile(String loginId, UserProfileInfoDTO dto);
    Map<String, Object> getUserProfile(String userId);

}
