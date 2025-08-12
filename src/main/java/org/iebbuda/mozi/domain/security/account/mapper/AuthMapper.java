package org.iebbuda.mozi.domain.security.account.mapper;

import org.apache.ibatis.annotations.Param;

public interface AuthMapper {
    void deleteAllAuthByUserId(@Param("userId") int userId);
}
