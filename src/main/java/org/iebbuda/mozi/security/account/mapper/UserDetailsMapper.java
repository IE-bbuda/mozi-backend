package org.iebbuda.mozi.security.account.mapper;

import org.iebbuda.mozi.user.domain.UserVO;

public interface UserDetailsMapper {
    public UserVO get(String LoginId);
}
