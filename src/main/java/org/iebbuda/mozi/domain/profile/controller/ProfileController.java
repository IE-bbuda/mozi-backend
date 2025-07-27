package org.iebbuda.mozi.domain.profile.controller;


import lombok.RequiredArgsConstructor;
import org.iebbuda.mozi.domain.profile.dto.UserProfileInfoDTO;
import org.iebbuda.mozi.domain.profile.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserProfile(@AuthenticationPrincipal UserDetails userDetails){
        String loginId = userDetails.getUsername();
        Map<String, Object> userProfile = userProfileService.getUserProfile(loginId);
        return ResponseEntity.ok(userProfile);
    }

    // 최종 추천 코드
    @PostMapping()
    public ResponseEntity<Map<String, Object>> saveProfile(
            @RequestBody  UserProfileInfoDTO data,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();
        Map<String, Object> response = userProfileService.saveProfile(userId, data);
        return ResponseEntity.ok(response);
    }

}
