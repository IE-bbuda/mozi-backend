package org.iebbuda.mozi.domain.goal.mapper;

import org.apache.ibatis.annotations.Param;
import org.iebbuda.mozi.domain.goal.domain.GoalVO;

import java.util.List;

public interface GoalMapper {

//    @Select("select * from UserGoal")
    public List<GoalVO> getList();

    public List<GoalVO> getListByUserId(int userId);

    public GoalVO get(int goalId);

    public void create(GoalVO goal);

    public int update(GoalVO goal);

    public int delete(int goalId);

    public GoalVO getBillionGoal(@Param("userId") int userId);

    List<GoalVO> findByUserId(int userId);


    /**
     * 사용자별 목표 개수 조회
     */
    int countByUserId(@Param("userId") int userId);

    /**
     * 사용자의 모든 목표 삭제 (회원탈퇴용)
     */
    int deleteByUserId(@Param("userId") int userId);
}
