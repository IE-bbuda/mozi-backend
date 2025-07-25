package org.iebbuda.mozi.user.mapper;


import org.apache.ibatis.annotations.Param;

import org.iebbuda.mozi.security.account.domain.AuthVO;
import org.iebbuda.mozi.user.domain.UserVO;

public interface UserMapper {
    UserVO findByUserId(int userId);
    UserVO findByLoginId(String loginId);
    int insert(UserVO userVO);
    int insertAuth(AuthVO auth);
    String findLoginIdByEmail(@Param("username")String username, @Param("email")String email);
    String findLoginIdByPhoneNumber(@Param("username")String username, @Param("phoneNumber")String phoneNumber);

}
