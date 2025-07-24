package org.iebbuda.mozi.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.user.dto.LoginIdFindByEmailRequestDTO;
import org.iebbuda.mozi.user.dto.LoginIdFindResponseDTO;
import org.iebbuda.mozi.user.dto.UserJoinRequestDTO;
import org.iebbuda.mozi.user.dto.UserJoinResponseDTO;
import org.iebbuda.mozi.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/check-username/{loginId}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String loginId){
        return ResponseEntity.ok().body(userService.checkDuplicate(loginId));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserJoinResponseDTO> signUpUser(@RequestBody UserJoinRequestDTO userJoinRequestDTO){
        return ResponseEntity.ok().body(userService.join(userJoinRequestDTO));
    }

    @PostMapping("/find-id/email")
    public ResponseEntity<LoginIdFindResponseDTO> findLoginIdByEmail(
            @RequestBody LoginIdFindByEmailRequestDTO request) {

        LoginIdFindResponseDTO response = userService.findLoginIdByEmail(
                request.getUsername(),
                request.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}
