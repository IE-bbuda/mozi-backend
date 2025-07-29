package org.iebbuda.mozi.domain.user.controller;


import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.domain.user.dto.request.MyPageUpdateRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.MyPageEditResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.MyPageResponseDTO;
import org.iebbuda.mozi.domain.user.service.MyPageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

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

    /**
     * 마이페이지 기본 정보 수정
     * PUT /api/mypage/edit
     */
    @PutMapping("/edit")
    public BaseResponse<MyPageResponseDTO> updateMyPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MyPageUpdateRequestDTO request) {
        String loginId = userDetails.getUsername();
        MyPageResponseDTO response = myPageService.updateMyPageInfo(loginId, request);
        return new BaseResponse<>(response);
    }
}
