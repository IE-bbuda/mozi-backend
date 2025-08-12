package org.iebbuda.mozi.domain.user.domain;


import lombok.Data;
import org.iebbuda.mozi.domain.security.account.domain.AuthVO;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private int userId;
    private String loginId;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String mainBank;
    private String birthDate;

    // OAuth 관련 필드들
    private String provider;      // "LOCAL", "KAKAO", "GOOGLE", "NAVER" 등
    private String providerId;    // OAuth 제공자의 사용자 ID

    private List<AuthVO> authList;


    private Boolean isDeleted;      // is_deleted 컬럼
    private LocalDateTime deletedAt; // deleted_at 컬럼
}
