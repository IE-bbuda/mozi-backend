package org.iebbuda.mozi.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.account.service.AccountService;
import org.iebbuda.mozi.domain.security.service.AuthService;
import org.iebbuda.mozi.domain.goal.service.GoalService;
import org.iebbuda.mozi.domain.profile.service.UserProfileService;
import org.iebbuda.mozi.domain.scrap.service.ScrapService;
import org.iebbuda.mozi.domain.security.service.oauth.OAuthUnlinkService;
import org.iebbuda.mozi.domain.user.domain.DeletedUserBackupVO;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.iebbuda.mozi.domain.user.dto.request.AccountRecoveryRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.WithdrawalRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.WithdrawalInfoDTO;
import org.iebbuda.mozi.domain.user.dto.response.WithdrawalResultDTO;
import org.iebbuda.mozi.domain.user.mapper.UserMapper;
import org.iebbuda.mozi.domain.user.mapper.WithdrawalMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Log4j2
public class WithdrawalService {
    private final UserMapper userMapper;
    private final WithdrawalMapper withdrawalMapper;
    private final PasswordEncoder passwordEncoder;

    private final ScrapService scrapService;
    private final GoalService goalService;
    private final AccountService accountService;
    private final UserProfileService userProfileService;
    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final OAuthUnlinkService oAuthUnlinkService;


    /**
     * 탈퇴 정보 조회 - 동기 처리 (화면은 즉시 보여져야 함)
     */
    public WithdrawalInfoDTO getWithdrawalInfo(String loginId) {
        UserVO user = findUserByLoginId(loginId);

        int scrapCount = scrapService.getTotalScrapCountByUserId(user.getUserId());
        int goalCount = goalService.getGoalCountByUserId(user.getUserId());

        return WithdrawalInfoDTO.of(user, scrapCount, goalCount);
    }

    /**
     * 탈퇴 처리 - 핵심 로직은 동기, 정리 작업만 비동기
     */
    @Transactional
    public WithdrawalResultDTO processWithdrawal(String loginId, WithdrawalRequestDTO request) {
        log.info("탈퇴 처리 시작 - loginId: {}", loginId);

        // 1. 사용자 조회 및 검증
        UserVO user = findUserByLoginId(loginId);

        // 2. 동의 확인
        if (!request.isAgreedToDataDeletion()) {
            throw new BaseException(BaseResponseStatus.DATA_DELETION_NOT_AGREED);
        }

        // 3. 사용자 타입별 처리
        boolean isOAuth = user.getProvider() != null && !"LOCAL".equals(user.getProvider());
        WithdrawalResultDTO result;

        if (!isOAuth) {
            result = processRegularWithdrawal(user, request);
        } else {
            result = processOAuthWithdrawal(user, request);
        }

        // 4. 백그라운드에서 관련 데이터 정리 (비동기)
        cleanupUserDataAsync(user.getUserId());

        log.info("탈퇴 처리 완료 - userId: {}", user.getUserId());
        return result;
    }

    /**
     * 일반 로그인 사용자 탈퇴
     */
    private WithdrawalResultDTO processRegularWithdrawal(UserVO user, WithdrawalRequestDTO request) {
        // 비밀번호 확인
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BaseException(BaseResponseStatus.PASSWORD_REQUIRED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(BaseResponseStatus.INVALID_PASSWORD);
        }

        // 핵심 탈퇴 처리 (즉시)
        withdrawalMapper.backupUserBeforeDelete(user.getUserId(), "REGULAR", "REGULAR", request.getReason());
        userMapper.maskDeletedUser(user.getUserId());

        return WithdrawalResultDTO.builder()
                .success(true)
                .message("탈퇴가 완료되었습니다.")
                .withdrawalType("REGULAR")
                .isRecoverable(true)
                .recoveryDeadline(LocalDateTime.now().plusDays(7))
                .nextSteps(Arrays.asList(
                        "7일 이내 아이디/비밀번호로 복구 가능합니다.",
                        "복구를 원하시면 로그인 페이지에서 '계정 복구'를 선택해주세요.",
                        "동일한 아이디로는 재가입이 불가능합니다."
                ))
                .build();
    }

    /**
     * OAuth 로그인 사용자 탈퇴
     */
    private WithdrawalResultDTO processOAuthWithdrawal(UserVO user, WithdrawalRequestDTO request) {
        String withdrawalType = request.getWithdrawalType();
        if (withdrawalType == null) {
            withdrawalType = "OAUTH_SERVICE_ONLY";
        }

        boolean isUnlink = "OAUTH_UNLINK".equals(withdrawalType);


        String providerName = getProviderName(user.getProvider());


        if (isUnlink) {

            // 완전 탈퇴 - 백업 없이 바로 삭제
            cleanupUserDataAsync(user.getUserId());  // 관련 데이터 정리
            unlinkOAuthSync(user.getProvider(), user.getProviderId());  // OAuth 연동 해제
            userMapper.hardDeleteUser(user.getUserId());  // 완전 삭제 (복구 불가)

            return WithdrawalResultDTO.builder()
                    .success(true)
                    .message(providerName + " 연동 해제 및 탈퇴가 완료되었습니다.")
                    .withdrawalType("OAUTH_UNLINK")
                    .isRecoverable(false)
                    .recoveryDeadline(null)
                    .nextSteps(Arrays.asList(
                            providerName + " 연동이 완전히 해제되었습니다.",
                            "계정 복구가 불가능합니다.",
                            "재가입 시 다시 " + providerName + " 동의 과정이 필요합니다."
                    ))
                    .build();
        } else {
            // 핵심 탈퇴 처리 (즉시)
            withdrawalMapper.backupUserBeforeDelete(user.getUserId(), "OAUTH", withdrawalType, request.getReason());

            userMapper.maskDeletedUser(user.getUserId()); // is_deleted = TRUE로 설정

            return WithdrawalResultDTO.builder()
                    .success(true)
                    .message("MoZi 서비스 탈퇴가 완료되었습니다.")
                    .withdrawalType("OAUTH_SERVICE_ONLY")
                    .isRecoverable(true)
                    .recoveryDeadline(LocalDateTime.now().plusDays(7))
                    .nextSteps(Arrays.asList(
                            providerName + " 계정은 그대로 유지됩니다.",
                            "다른 " + providerName + " 연동 앱들은 영향받지 않습니다.",
                            "7일 이내 같은 " + providerName + " 계정으로 로그인 시 복구 가능합니다."
                    ))
                    .build();
        }
    }

    /**
     * 탈퇴 가능 여부 확인
     */
    public boolean canUserWithdraw(String loginId) {
        UserVO user = findUserByLoginId(loginId);

        // 예시: 가입 후 24시간 이내에는 탈퇴 불가
        LocalDateTime joinDate = user.getCreatedAt();
        LocalDateTime oneDayAfterJoin = joinDate.plusDays(1);
        boolean canWithdraw = LocalDateTime.now().isAfter(oneDayAfterJoin);

        log.info("탈퇴 가능 여부 확인 - loginId: {}, canWithdraw: {}", loginId, canWithdraw);
        return canWithdraw;
    }
    /**
     * 비동기 데이터 정리 - Service 의존성 활용
     */
    @Async("withdrawalTaskExecutor")
    public void cleanupUserDataAsync(int userId) {
        try {
            log.info("사용자 관련 데이터 정리 시작 - userId: {}", userId);

            //  Service 메서드 호출 (비즈니스 로직 + 트랜잭션 + 로깅 포함)
            scrapService.deleteAllScrapsByUserId(userId);           // 모든 스크랩 삭제
            accountService.deleteAllAccountsByUserId(userId);       // 계좌 데이터 삭제
            accountService.deleteAllBankLoginsByUserId(userId);   // 은행 연동 삭제
            goalService.deleteAllGoalsByUserId(userId);             // 목표 삭제
            userProfileService.deleteProfileByUserId(userId);       // 프로필 삭제
            authService.deleteAllAuthByUserId(userId);              // 권한 삭제
            passwordResetService.deleteSessionsByUserId(userId);    // 세션 삭제

            log.info("사용자 관련 데이터 정리 완료 - userId: {}", userId);

        } catch (Exception e) {
            log.error("데이터 정리 실패 - userId: {}, 수동 정리 필요", userId, e);
            // 실패해도 탈퇴는 이미 완료된 상태
        }
    }

    /**
     * 계정 복구 - 동기 처리
     */
    @Transactional
    public void recoverAccount(AccountRecoveryRequestDTO request) {
        log.info("계정 복구 시도 - loginId: {}", request.getLoginId());

        DeletedUserBackupVO backup = withdrawalMapper.findRecoverableUser(request.getLoginId());
        if (backup == null) {
            throw new BaseException(BaseResponseStatus.USER_NOT_FOUND);
        }

        // 복구 가능성 확인
        if (!canRecover(backup)) {
            log.warn("복구 불가능한 계정 - loginId: {}, deadline: {}",
                    request.getLoginId(), backup.getRecoveryDeadline());
            throw new BaseException(BaseResponseStatus.ACCOUNT_NOT_RECOVERABLE);
        }

        // 일반 로그인 사용자인 경우 비밀번호 확인
        if (isRegularUser(backup)) {
            if (request.getPassword() == null ||
                    !passwordEncoder.matches(request.getPassword(), backup.getOriginalPassword())) {
                log.warn("계정 복구 시 비밀번호 불일치 - loginId: {}", request.getLoginId());
                throw new BaseException(BaseResponseStatus.INVALID_PASSWORD);
            }
        }

        // 복구 처리
        userMapper.restoreUser(backup);
        withdrawalMapper.deleteBackupData(backup.getOriginalUserId());

        log.info("계정 복구 완료 - userId: {}", backup.getOriginalUserId());
    }

    /**
     * OAuth 연동 해제 (동기 처리)
     * OAuthUnlinkService를 사용하여 실제 연동 해제 처리
     */
    private boolean unlinkOAuthSync(String provider, String providerId) {
        try {
            log.info("OAuth 연동 해제 시작 - provider: {}, providerId: {}", provider, providerId);

            boolean success = oAuthUnlinkService.unlinkOAuth(provider, providerId);

            if (success) {
                log.info("OAuth 연동 해제 성공 - provider: {}", provider);
            } else {
                log.warn("OAuth 연동 해제 실패 - provider: {}, 수동 확인 필요", provider);
            }

            return success;

        } catch (Exception e) {
            log.error("OAuth 연동 해제 중 예외 발생 - provider: {}", provider, e);
            return false;
        }
    }

    /**
     * 복구 가능한지 확인
     */
    private boolean canRecover(DeletedUserBackupVO backup) {
        return backup.getIsRecoverable() != null && backup.getIsRecoverable() &&
                backup.getRecoveryDeadline() != null &&
                backup.getRecoveryDeadline().isAfter(LocalDateTime.now());
    }

    /**
     * OAuth 사용자인지 확인
     */
    private boolean isOAuthUser(DeletedUserBackupVO backup) {
        return "OAUTH".equals(backup.getUserType());
    }

    /**
     * 일반 사용자인지 확인
     */
    private boolean isRegularUser(DeletedUserBackupVO backup) {
        return "REGULAR".equals(backup.getUserType());
    }

    private UserVO findUserByLoginId(String loginId) {
        UserVO user = userMapper.findByLoginId(loginId);
        if (user == null) {
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER);
        }
        return user;
    }

    private String getProviderName(String provider) {
        if (provider == null || "LOCAL".equals(provider)) return "일반";
        switch (provider.toUpperCase()) {
            case "KAKAO": return "카카오";
            case "GOOGLE": return "구글";
            case "NAVER": return "네이버";
            default: return provider;
        }
    }

}
