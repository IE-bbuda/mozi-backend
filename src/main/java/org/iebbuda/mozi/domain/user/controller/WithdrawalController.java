package org.iebbuda.mozi.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.domain.user.dto.request.AccountRecoveryRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.WithdrawalRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.WithdrawalInfoDTO;
import org.iebbuda.mozi.domain.user.dto.response.WithdrawalResultDTO;
import org.iebbuda.mozi.domain.user.service.WithdrawalService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/withdrawal")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    /**
     * 탈퇴 정보 조회
     * GET /api/withdrawal/info
     */
    @GetMapping("/info")
    public BaseResponse<WithdrawalInfoDTO> getWithdrawalInfo(
            @AuthenticationPrincipal UserDetails userDetails) {

        WithdrawalInfoDTO withdrawalInfo = withdrawalService.getWithdrawalInfo(userDetails.getUsername());
        return new BaseResponse<>(withdrawalInfo);
    }


    /**
     * 회원 탈퇴 처리
     * POST /api/withdrawal
     */
    @PostMapping
    public BaseResponse<WithdrawalResultDTO> processWithdrawal(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WithdrawalRequestDTO request) {

        WithdrawalResultDTO result = withdrawalService.processWithdrawal(userDetails.getUsername(), request);
        return new BaseResponse<>(result);
    }

    /**
     * 계정 복구
     * POST /api/withdrawal/recovery
     */
    @PostMapping("/recovery")
    public BaseResponse<String> recoverAccount(@RequestBody AccountRecoveryRequestDTO request) {
        withdrawalService.recoverAccount(request);
        return new BaseResponse<>("계정이 성공적으로 복구되었습니다.");
    }

    /**
     * 탈퇴 가능 여부 확인 (선택적)
     * GET /api/withdrawal/check
     */
    @GetMapping("/check")
    public BaseResponse<Boolean> checkWithdrawalAvailability(
            @AuthenticationPrincipal UserDetails userDetails) {

        // 간단한 확인 로직 (예: 최근 가입자는 24시간 후 탈퇴 가능 등)
        boolean canWithdraw = withdrawalService.canUserWithdraw(userDetails.getUsername());
        return new BaseResponse<>(canWithdraw);
    }
}
