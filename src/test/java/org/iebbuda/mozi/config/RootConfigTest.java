package org.iebbuda.mozi.config;

import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.iebbuda.mozi.domain.security.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})  // SecurityConfig 추가
@Log4j2
class RootConfigTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    @DisplayName("DataSource 연결이 된다.")
    public void dataSource() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            log.info("DataSource 준비 완료");
            log.info(conn);
        }
    }

    @Test
    public void testSqlSessionFactory(){
        try(SqlSession session = sqlSessionFactory.openSession();
            Connection conn = session.getConnection();
            ){
            log.info(session);
            log.info(conn);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}