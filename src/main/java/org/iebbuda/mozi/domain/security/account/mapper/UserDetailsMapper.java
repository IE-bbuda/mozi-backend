package org.iebbuda.mozi.domain.security.account.mapper;

import org.iebbuda.mozi.domain.user.domain.UserVO;

public interface UserDetailsMapper {
    public UserVO get(String LoginId);
}
