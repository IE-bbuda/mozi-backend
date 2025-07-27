package org.iebbuda.mozi.domain.user.service;



import org.iebbuda.mozi.domain.user.dto.request.UserJoinRequestDTO;
import org.iebbuda.mozi.domain.user.dto.response.LoginIdFindResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.UserDTO;
import org.iebbuda.mozi.domain.user.dto.response.UserJoinResponseDTO;


import java.util.Optional;

public interface UserService {
    boolean checkDuplicate(String loginId);
    Optional<UserDTO> get(int userId);
    UserJoinResponseDTO join(UserJoinRequestDTO dto);
    LoginIdFindResponseDTO findLoginIdByEmail(String username, String email);
    LoginIdFindResponseDTO findLoginIdByPhoneNumber(String username, String phoneNumber);

}
