package org.iebbuda.mozi.domain.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.user.domain.DeletedUserBackupVO;

import java.util.List;

public interface WithdrawalMapper {

    // 탈퇴 처리
    void backupUserBeforeDelete(@Param("userId") int userId,
                                @Param("userType") String userType,
                                @Param("withdrawalType") String withdrawalType,
                                @Param("reason") String reason);



    // 복구 관련
    DeletedUserBackupVO findRecoverableUser(@Param("loginId") String loginId);
    DeletedUserBackupVO findRecoverableOAuthUser(@Param("provider") String provider,
                                                 @Param("providerId") String providerId);

    
    void deleteBackupData(@Param("userId") int userId);

    // 완전 삭제
    List<Integer> findExpiredUsers();

    void deleteExpiredBackups();
}
