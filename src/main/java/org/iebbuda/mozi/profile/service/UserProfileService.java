package org.iebbuda.mozi.profile.service;

import org.iebbuda.mozi.profile.dto.UserProfileInfoDTO;

public interface ProfileService {
    void saveSurvey(int userId, UserProfileInfoDTO dto);
}
