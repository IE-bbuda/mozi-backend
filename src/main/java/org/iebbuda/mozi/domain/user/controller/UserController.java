package org.iebbuda.mozi.domain.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.domain.user.dto.request.LoginIdFindByEmailRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.PasswordResetRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.PasswordResetVerifyRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.LoginIdFindResponseDTO;
import org.iebbuda.mozi.domain.user.dto.request.UserJoinRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.PasswordResetResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.PasswordResetVerifyResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.UserJoinResponseDTO;

import org.iebbuda.mozi.domain.user.service.PasswordResetService;

import org.iebbuda.mozi.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;


    @GetMapping("/check-username/{loginId}")
    public BaseResponse<Boolean> checkUsername(@PathVariable String loginId){
        boolean result = userService.checkDuplicate(loginId);
        return new BaseResponse<>(result);

    }

    @PostMapping("/signup")
    public ResponseEntity<UserJoinResponseDTO> signUpUser(@RequestBody UserJoinRequestDTO userJoinRequestDTO){
        return ResponseEntity.ok().body(userService.join(userJoinRequestDTO));
    }


    @PostMapping("/find-id")
    public ResponseEntity<LoginIdFindResponseDTO> findLoginIdByEmail(
            @RequestBody LoginIdFindByEmailRequestDTO request) {

        LoginIdFindResponseDTO response = userService.findLoginIdByEmail(
                request.getUsername(),
                request.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/verify")
    public ResponseEntity<PasswordResetVerifyResponseDTO> verifyAccount(
            @RequestBody PasswordResetVerifyRequestDTO request) {

        PasswordResetVerifyResponseDTO response = passwordResetService.verifyAccount(request);
        return ResponseEntity.ok(response);  //대부분 오류 그냥 200 처리
    }

    /**
     * 2단계: 새 비밀번호 설정
     * POST /api/password-reset/reset
     */
    @PostMapping("/password/reset")
    public ResponseEntity<PasswordResetResponseDTO> resetPassword(
            @RequestBody PasswordResetRequestDTO request) {

        PasswordResetResponseDTO response = passwordResetService.resetPassword(request);
        return ResponseEntity.ok(response); //대부분 오류 그냥 200 처리

    }

}
