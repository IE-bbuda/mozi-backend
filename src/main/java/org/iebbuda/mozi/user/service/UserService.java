package org.iebbuda.mozi.user.service;

import org.iebbuda.mozi.user.dto.UserDTO;
import org.iebbuda.mozi.user.dto.UserJoinResponseDTO;
import org.iebbuda.mozi.user.dto.UserJoinRequestDTO;

import java.util.Optional;

public interface UserService {
    boolean checkDuplicate(String loginId);
    Optional<UserDTO> get(int userId);
    UserJoinResponseDTO join(UserJoinRequestDTO dto);
}
