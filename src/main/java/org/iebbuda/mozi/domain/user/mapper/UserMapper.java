package org.iebbuda.mozi.domain.user.mapper;


import org.apache.ibatis.annotations.Param;

import org.iebbuda.mozi.domain.security.account.domain.AuthVO;
import org.iebbuda.mozi.domain.user.domain.UserVO;

import java.time.LocalDateTime;

public interface UserMapper {
    UserVO findByUserId(int userId);
    UserVO findByLoginId(String loginId);
    int insert(UserVO userVO);
    int insertAuth(AuthVO auth);

    String findLoginIdByEmail(@Param("username")String username, @Param("email")String email);
    String findLoginIdByPhoneNumber(@Param("username")String username, @Param("phoneNumber")String phoneNumber);

    /**
     * 로그인ID와 이메일로 사용자 찾기
     * @param loginId 로그인 아이디
     * @param email 이메일
     * @return 사용자 정보 (없으면 null)
     */
    UserVO findByLoginIdAndEmail(@Param("loginId") String loginId, @Param("email") String email);

    /**
     * 사용자 비밀번호 업데이트
     * @param userId 사용자 ID
     * @param password 새 비밀번호
     * @param updatedAt 수정 시간
     * @return 업데이트된 행 수
     */
    int updateUserPassword(@Param("userId") int userId,
                           @Param("password") String password,
                           @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 마이페이지 비밀번호 변경 (로그인된 상태)
     * @param loginId 로그인 ID
     * @param newPassword 새 비밀번호
     * @return 업데이트된 행 수
     */
    int updatePasswordByLoginId(@Param("loginId") String loginId,
                                @Param("newPassword") String newPassword);

    /**
     * 사용자 기본 정보 수정 (이메일, 전화번호)
     */
    int updateUserInfo(@Param("loginId") String loginId,
                       @Param("email") String email,
                       @Param("phoneNumber") String phoneNumber);

}
