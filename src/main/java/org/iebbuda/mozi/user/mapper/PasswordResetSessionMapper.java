package org.iebbuda.mozi.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.user.domain.PasswordResetSessionVO;

import java.time.LocalDateTime;

public interface PasswordResetSessionMapper {


    /**
     * 특정 사용자의 기존 세션들 삭제
     * @param userId 사용자 ID
     * @return 삭제된 행 수
     */
    int deleteSessionsByUserId(@Param("userId") int userId);

    /**
     * 새 비밀번호 재설정 세션 생성
     * @param session 세션 정보
     * @return 삽입된 행 수
     */
    int insertPasswordResetSession(PasswordResetSessionVO session);

    /**
     * 유효한 세션 조회 (만료되지 않고 완료되지 않은)
     * @param token 토큰
     * @param now 현재 시간
     * @return 세션 정보 (없으면 null)
     */
    PasswordResetSessionVO findValidSession(@Param("token") String token, @Param("now") LocalDateTime now);



    /**
     * 세션을 완료 상태로 변경
     * @param token 토큰
     * @return 업데이트된 행 수
     */
    int markSessionAsCompleted(@Param("token") String token);

    /**
     * 만료된 세션들 삭제
     * @param now 현재 시간
     * @return 삭제된 행 수
     */
    int deleteExpiredSessions(@Param("now") LocalDateTime now);
}
