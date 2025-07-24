package org.iebbuda.mozi.user.mapper;

import org.iebbuda.mozi.security.account.domain.AuthVO;
import org.iebbuda.mozi.user.domain.UserVO;

public interface UserMapper {
    UserVO findByUserId(int userId);
    UserVO findByLoginId(String loginId);
    int insert(UserVO userVO);
    int insertAuth(AuthVO auth);
}
