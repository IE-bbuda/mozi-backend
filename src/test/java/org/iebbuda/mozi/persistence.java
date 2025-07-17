package org.iebbuda.mozi;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.fail;

@Log4j2

public class persistence {

    // Properties 파일에서 읽기


    @BeforeAll
    public static void setup(){
        try{
            Properties props = new Properties();
            props.load(persistence.class.getResourceAsStream("/application.properties"));
            // 값 설정
            String driver = props.getProperty("jdbc.test.driver");
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("JDBC 드라이버 연결이 된다.")
    public void testConnection(){
        try {
            // Properties 파일에서 값 읽기
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/application.properties"));

            String url = props.getProperty("jdbc.test.url");
            String username = props.getProperty("jdbc.test.username");
            String password = props.getProperty("jdbc.test.password");

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                log.info(conn);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}


