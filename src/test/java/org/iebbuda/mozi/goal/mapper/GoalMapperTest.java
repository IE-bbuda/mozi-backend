package org.iebbuda.mozi.goal.mapper;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.goal.domain.GoalVO;
import org.iebbuda.mozi.security.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = { RootConfig.class })
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})  // SecurityConfig 추가
@Log4j2
class GoalMapperTest {

    @Autowired
    private GoalMapper mapper;

    @Test
    @DisplayName("GoalMapper의 목록")
    public void getList(){
        for(GoalVO goal : mapper.getList()){
            log.info(goal);
        }
    }

    @Test
    @DisplayName("GoalMapper의 특정 게시글 읽기")
    public void get() {
        GoalVO goal = mapper.get(1);
        log.info(goal);
    }

    @Test
    @DisplayName("GoalMapper의 새 목표 작성")
    public void create() {
        GoalVO goal = new GoalVO();

        goal.setUserId(1);
        goal.setGoalName("테스트 목표명");
        goal.setKeyword(GoalVO.GoalKeyword.HOBBY);
        goal.setTargetAmount(BigDecimal.valueOf(1000000));
        goal.setGoalDate(LocalDateTime.of(2025, 7, 25, 0, 0));
        goal.setMemo("테스트 메모");
        goal.setGoalStatus(true);

        mapper.create(goal);
        log.info(goal);
    }

    @Test
    @DisplayName("GoalMapper의 목표 수정")
    public void update() {
        GoalVO goal = new GoalVO();

        goal.setGoalId(3);
        goal.setGoalName("수정된 목표 제목");
        goal.setKeyword(GoalVO.GoalKeyword.TRAVEL);
        goal.setTargetAmount(BigDecimal.valueOf(2000000));
        goal.setGoalDate(LocalDateTime.of(2030, 7, 25, 0, 0));
        goal.setMemo("수정된 메모");
        goal.setGoalStatus(false);

        int count = mapper.update(goal);
        log.info("UPDATE COUNT: " + count);
    }

    @Test
    @DisplayName("GoalMapper의 목표 삭제")
    public void delete() {
        log.info("DELETE COUNT: " + mapper.delete(4));
    }

}