package org.iebbuda.mozi.domain.user.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequestDTO {
    private String password;              // 일반 로그인 사용자용
    private String reason;                // 탈퇴 사유
    private String withdrawalType;        // 탈퇴 방식 (REGULAR, OAUTH_SERVICE_ONLY, OAUTH_UNLINK)
    private boolean agreedToDataDeletion; // 데이터 삭제 동의
}
