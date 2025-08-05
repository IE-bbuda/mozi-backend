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

}
