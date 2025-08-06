package org.iebbuda.mozi.domain.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.iebbuda.mozi.domain.user.dto.request.EmailCodeVerifyRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.MyPageUpdateRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.PasswordConfirmRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.MyPageEditResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.MyPageResponseDTO;
import org.iebbuda.mozi.domain.user.service.MyPageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Log4j2
public class MyPageController {

    private final MyPageService myPageService;


    // Controller에 추가할 메서드
    /**
     * 마이페이지 수정 전 비밀번호 확인
     * POST /api/mypage/confirm-password
     */
    @PostMapping("/confirm-password")
    public BaseResponse<String> confirmPassword(
            @RequestBody PasswordConfirmRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String loginId = userDetails.getUsername();
        myPageService.confirmPassword(loginId, request.getPassword());
        return new BaseResponse<>(true,200,"비밀번호 확인이 완료되었습니다.");
    }

    /**
     * 마이페이지 조회 - 기본 정보 + 프로필 정보 통합
     * GET /api/mypage
     */
    @GetMapping
    public BaseResponse<MyPageResponseDTO> getMyPage(
            @AuthenticationPrincipal UserDetails userDetails) {
        String loginId = userDetails.getUsername();
        MyPageResponseDTO response = myPageService.getMyPageInfo(loginId);
        return new BaseResponse<>(response);
    }

    /**
     * 마이페이지 수정 화면용 정보 조회
     * GET /api/mypage/edit
     */
    @GetMapping("/edit")
    public BaseResponse<MyPageEditResponseDTO> getMyPageEdit(
            @AuthenticationPrincipal UserDetails userDetails) {
        String loginId = userDetails.getUsername();
        MyPageEditResponseDTO response = myPageService.getMyPageEditInfo(loginId);
        return new BaseResponse<>(response);
    }

    // ========== 이메일 인증 관련 API ==========

    /**
     * 마이페이지 이메일 변경용 인증번호 발송
     * POST /api/mypage/send-email-code
     */
    @PostMapping("/send-email-code")
    public BaseResponse<String> sendMyPageEmailCode(
            @RequestParam String email,
            @AuthenticationPrincipal UserDetails userDetails) {

        String loginId = userDetails.getUsername();
        myPageService.sendMyPageEmailVerification(loginId, email);

        return new BaseResponse<>(true, 200, "인증번호가 발송되었습니다.");
    }

    /**
     * 마이페이지 이메일 변경용 인증번호 확인
     * POST /api/mypage/verify-email-code
     */
    @PostMapping("/verify-email-code")
    public BaseResponse<String> verifyMyPageEmailCode(
            @RequestBody EmailCodeVerifyRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String loginId = userDetails.getUsername();
        myPageService.verifyMyPageEmailCode(loginId, request);

        return new BaseResponse<>(true, 200, "이메일 인증이 완료되었습니다.");
    }

    /**
     * 마이페이지 기본 정보 수정
     * PUT /api/mypage/edit
     */
    @PutMapping("/edit")
    public BaseResponse<BaseResponseStatus> updateMyPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MyPageUpdateRequestDTO request) {
        String loginId = userDetails.getUsername();
        myPageService.updateMyPageInfo(loginId, request);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

}
