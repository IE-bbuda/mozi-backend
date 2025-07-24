package org.iebbuda.mozi.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.security.account.domain.AuthVO;
import org.iebbuda.mozi.security.account.domain.UserRole;
import org.iebbuda.mozi.user.domain.UserVO;
import org.iebbuda.mozi.user.dto.LoginIdFindResponseDTO;
import org.iebbuda.mozi.user.dto.UserDTO;
import org.iebbuda.mozi.user.dto.UserJoinResponseDTO;
import org.iebbuda.mozi.user.dto.UserJoinRequestDTO;
import org.iebbuda.mozi.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;


    //로그인Id 중복 체크
    @Override
    public boolean checkDuplicate(String loginId) {
        return mapper.findByLoginId(loginId) ==null;
    }

    @Override
    public Optional<UserDTO> get(int userId) {
        UserVO user = mapper.findByUserId(userId);

        return Optional.ofNullable(user).map(UserDTO::of);
    }

    @Transactional
    @Override
    public UserJoinResponseDTO join(UserJoinRequestDTO dto) {
        //중복 체크
//        if(checkDuplicate(dto.getLoginId())){
//            log.warn("중복된 아이디로 가입 시도: " + dto.getLoginId());
//            throw new DuplicateLoginIdException("이미 존재하는 아이디입니다.");
//        }

        UserVO user = dto.toVO(passwordEncoder);
        mapper.insert(user);

        AuthVO auth = new AuthVO(user.getUserId(), UserRole.ROLE_USER);
        mapper.insertAuth(auth);

        log.info("회원가입 완료: userId = " + user.getUserId());
        return UserJoinResponseDTO.of(user);
    }

    @Override
    public LoginIdFindResponseDTO findLoginIdByEmail(String username, String email) {
        Optional<String> loginId = Optional.ofNullable(mapper.findLoginIdByEmail(username, email));
        return loginId
                .map(this::maskLoginId)
                .map(LoginIdFindResponseDTO::success)
                .orElse(LoginIdFindResponseDTO.notFound());
    }

    @Override
    public LoginIdFindResponseDTO findLoginIdByPhoneNumber(String username, String phoneNumber) {
        Optional<String> loginId = Optional.ofNullable(mapper.findLoginIdByPhoneNumber(username, phoneNumber));
        return loginId
                .map(this::maskLoginId)
                .map(LoginIdFindResponseDTO::success)
                .orElse(LoginIdFindResponseDTO.notFound());
    }

    private String maskLoginId(String loginId) {
        if (loginId.length() <= 2) {
            return "*".repeat(loginId.length());
        }
        if (loginId.length() <= 6) {
            return loginId.substring(0, 2) + "***";
        }
        // 긴 아이디는 앞 4글자 + *** + 끝 1글자
        return loginId.substring(0, 4) + "***" + loginId.charAt(loginId.length() - 1);
    }
}
