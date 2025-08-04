package org.iebbuda.mozi.domain.profile.controller;


import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.common.response.BaseResponse;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.profile.domain.UserProfileVO;
import org.iebbuda.mozi.domain.profile.dto.UserProfileInfoDTO;
import org.iebbuda.mozi.domain.profile.mapper.UserProfileMapper;
import org.iebbuda.mozi.domain.profile.service.UserProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;




@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public BaseResponse<UserProfileInfoDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails){
        String loginId = userDetails.getUsername();
        UserProfileInfoDTO userProfile = userProfileService.getUserProfile(loginId);
        return new BaseResponse<>(userProfile);
    }

    // 최종 추천 코드
    @PostMapping
    public BaseResponse<BaseResponseStatus> saveProfile(
            @RequestBody  UserProfileInfoDTO data,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();
        userProfileService.saveProfile(userId, data);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    // 정책 페이지에서 테스트용: userId로 직접 퍼스널 정보 조회 (인증 없음)
    private final UserProfileMapper userProfileMapper;
    @GetMapping("/test/{userId}")
    public BaseResponse<UserProfileInfoDTO> getUserProfileByUserId(@PathVariable int userId) {
        UserProfileVO vo = userProfileMapper.findByUserId(userId);
        return new BaseResponse<>(UserProfileInfoDTO.of(vo));
    }


}
