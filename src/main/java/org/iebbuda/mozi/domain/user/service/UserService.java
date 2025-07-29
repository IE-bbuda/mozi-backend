package org.iebbuda.mozi.domain.user.service;



import org.iebbuda.mozi.domain.user.dto.request.EmailCodeVerifyRequestDTO;
import org.iebbuda.mozi.domain.user.dto.request.UserJoinRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.LoginIdFindResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.UserDTO;


import java.util.Optional;

public interface UserService {
    boolean checkDuplicate(String loginId);
    public boolean checkEmailDuplicate(String email);
    Optional<UserDTO> get(int userId);
    int join(UserJoinRequestDTO dto);
    LoginIdFindResponseDTO findLoginIdByEmail(String username, String email);


    // 회원가입 관련 이메일 인증 메서드 추가
    void sendSignupEmailVerification(String email);
    void verifySignupEmailCode(EmailCodeVerifyRequestDTO request);
}
