package org.iebbuda.mozi.profile.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.profile.domain.UserProfileVO;

public interface UserProfileMapper {

    /**
     * 퍼스널 정보 입력 여부 확인
     */
    boolean hasPersonalInfo(@Param("userId") int userId);

    UserProfileVO findByUserId(@Param("userId") int userId);

    int updateUserProfile(UserProfileVO userProfile);
    int insertUserProfile(UserProfileVO userProfile);
}
