package org.iebbuda.mozi.goal.mapper;

import org.apache.ibatis.annotations.Select;
import org.iebbuda.mozi.goal.domain.GoalVO;

import java.util.List;

public interface GoalMapper {

//    @Select("select * from UserGoal")
    public List<GoalVO> getList();

    public GoalVO get(int goalId);

    public void create(GoalVO goal);

    public int update(GoalVO goal);

    public int delete(int goalId);

}
