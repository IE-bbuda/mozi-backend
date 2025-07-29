package org.iebbuda.mozi.domain.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.user.dto.request.PasswordResetRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.PasswordResetVerifyRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.LoginIdFindResponseDTO;
import org.iebbuda.mozi.domain.user.dto.request.UserJoinRequestDTO;

import org.iebbuda.mozi.domain.user.service.PasswordResetService;

import org.iebbuda.mozi.domain.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;
    private final PasswordResetService passwordResetService;

    /**
     * 로그인 ID 중복 확인
     * @param loginId 확인할 로그인 ID
     * @return 중복 여부 (true: 사용 가능, false: 중복)
     */
    @GetMapping("/check-username/{loginId}")
    public BaseResponse<Boolean> checkUsername(@PathVariable String loginId){
        boolean result = userService.checkDuplicate(loginId);
        return new BaseResponse<>(result);
    }

    /**
     * 회원가입
     * @param userJoinRequestDTO 회원가입 정보 (로그인ID, 비밀번호, 이름, 이메일 등)
     * @return 가입 성공 여부
     */
    @PostMapping("/signup")
    public BaseResponse<BaseResponseStatus> signUpUser(@RequestBody UserJoinRequestDTO userJoinRequestDTO){
        userService.join(userJoinRequestDTO);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    /**
     * 로그인 ID 찾기
     * @param username 사용자 이름
     * @param email 이메일 주소
     * @return 마스킹된 로그인 ID 정보
     */
    @GetMapping("/find-id")
    public BaseResponse<LoginIdFindResponseDTO> findLoginIdByEmail(
            @RequestParam String username, @RequestParam String email) {

        LoginIdFindResponseDTO response = userService.findLoginIdByEmail(username, email);
        return new BaseResponse<>(response);
    }

    /**
     * 비밀번호 재설정 1단계: 계정 검증 및 토큰 발급
     * @param request 로그인 ID와 이메일 정보
     * @return 검증 결과 및 재설정 토큰
     */
    @PostMapping("/password/verify")
    public BaseResponse<String> verifyAccount(
            @RequestBody PasswordResetVerifyRequestDTO request) {

       String token = passwordResetService.verifyAccount(request);
        return new BaseResponse<>(token);
    }

    /**
     * 비밀번호 재설정 2단계: 새 비밀번호 설정
     * @param request 재설정 토큰과 새 비밀번호 정보
     * @return 비밀번호 변경 결과
     */
    @PostMapping("/password/reset")
    public BaseResponse<BaseResponseStatus> resetPassword(
            @RequestBody PasswordResetRequestDTO request) {

       passwordResetService.resetPassword(request);
        return new BaseResponse<>(BaseResponseStatus.PASSWORD_RESET_SUCCESS);
    }
}
