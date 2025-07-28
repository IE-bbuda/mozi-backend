package org.iebbuda.mozi.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.common.response.BaseException;
import org.iebbuda.mozi.common.response.BaseResponseStatus;
import org.iebbuda.mozi.domain.security.account.domain.AuthVO;
import org.iebbuda.mozi.domain.security.account.domain.UserRole;
import org.iebbuda.mozi.domain.user.domain.UserVO;


import org.iebbuda.mozi.domain.user.dto.response.LoginIdFindResponseDTO;
import org.iebbuda.mozi.domain.user.dto.response.UserDTO;
import org.iebbuda.mozi.domain.user.dto.request.UserJoinRequestDTO;

import org.iebbuda.mozi.domain.user.mapper.UserMapper;
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
        log.info("중복 확인 요청 - 로그인ID: {}", loginId);
        boolean available = mapper.findByLoginId(loginId) == null;
        log.info("중복 확인 결과 - 로그인ID: {}, 사용가능: {}", loginId, available);
        return available;
    }

    @Override
    public Optional<UserDTO> get(int userId) {
        UserVO user = mapper.findByUserId(userId);
        log.debug("사용자 조회 완료 - ID: {}, 사용자 존재 여부: {}", userId, user != null);
        return Optional.ofNullable(user).map(UserDTO::of);
    }

    @Transactional
    @Override
    public int join(UserJoinRequestDTO dto) {
        log.info("회원가입 시작 - 로그인 ID: {}", dto.getLoginId());

        // 중복 체크
        if (!checkDuplicate(dto.getLoginId())) {
            log.warn("중복된 아이디로 가입 시도: {}", dto.getLoginId());
            throw new BaseException(BaseResponseStatus.DUPLICATE_LOGIN_ID);
        }

        UserVO user = dto.toVO(passwordEncoder);
        mapper.insert(user);

        AuthVO auth = new AuthVO(user.getUserId(), UserRole.ROLE_USER);
        mapper.insertAuth(auth);

        log.info("회원가입 완료 - userId: {}, loginId: {}", user.getUserId(), dto.getLoginId());
        return user.getUserId();
    }


    @Override
    public LoginIdFindResponseDTO findLoginIdByEmail(String username, String email) {
        log.info("ID 찾기 요청 - username: {}, email: {}", username, email);

        Optional<String> loginId = Optional.ofNullable(mapper.findLoginIdByEmail(username, email));

        if (loginId.isPresent()) {
            log.info("ID 찾기 성공 - username: {}", username);
            return LoginIdFindResponseDTO.success(loginId.map(this::maskLoginId).get());
        } else {
            log.warn("ID 찾기 실패 - 일치하는 정보 없음, username: {}, email: {}", username, email);
            return LoginIdFindResponseDTO.notFound();
        }
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
