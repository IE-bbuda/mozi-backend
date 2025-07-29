package org.iebbuda.mozi.domain.security.account.mapper;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.config.RootConfig;
import org.iebbuda.mozi.domain.security.account.domain.AuthVO;
import org.iebbuda.mozi.domain.security.account.mapper.UserDetailsMapper;
import org.iebbuda.mozi.domain.security.config.SecurityConfig;
import org.iebbuda.mozi.domain.user.domain.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class})
@Log4j2
@Transactional
class UserDetailsMapperTest {
    @Autowired
    private UserDetailsMapper mapper;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUpTestUser() throws Exception {
        try (Connection conn = dataSource.getConnection()) {

            // 1. 유저 ID 가져오기
            String findUserIdSql = "SELECT user_id FROM User WHERE login_id = ?";
            Integer userId = null;

            try (PreparedStatement pstmt = conn.prepareStatement(findUserIdSql)) {
                pstmt.setString(1, "test_alice123");
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    }
                }
            }

            if (userId != null) {
                // 2. Auth 먼저 삭제
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Auth WHERE user_id = ?")) {
                    pstmt.setInt(1, userId);
                    pstmt.executeUpdate();
                }

                // 3. User 삭제
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM User WHERE user_id = ?")) {
                    pstmt.setInt(1, userId);
                    pstmt.executeUpdate();
                }

                log.info("BeforeEach: 기존 테스트 유저와 권한 삭제 완료 (user_id={})", userId);
            }
        }
    }


    @Test
    public void get() throws Exception {
        // Given - 테스트 데이터 삽입
        int insertedUserId = insertTestData();
        log.info("테스트용 user_id: {}", insertedUserId);

        // When - 실제 테스트
        UserVO user = mapper.get("test_alice123");
        log.info("조회된 user = {}", user);


        // Then - 검증
        assertNotNull(user, "사용자가 조회되어야 함");
        assertEquals("test_alice123", user.getLoginId());
        assertNotNull(user.getAuthList(), "권한 목록이 있어야 함");
        assertTrue(user.getAuthList().size() == 2, "권한이 2개");

        for (AuthVO auth : user.getAuthList()) {
            log.info("권한 = {}", auth);
            assertNotNull(auth.getAuth());
            assertEquals(insertedUserId, auth.getUserId());
        }

        // 메서드 끝나면 @Rollback에 의해 자동으로 모든 데이터가 롤백됨
    }

    private int insertTestData() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            int insertedUserId = -1;

            // 1. User 데이터 삽입 및 생성된 ID 가져오기
            String userSql = """
                INSERT INTO User (username,login_id, password, phone_number, email, create_at, updated_at, main_bank, birth_date)
                VALUES (?,?, ?, ?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement pstmt = conn.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "alice");
                pstmt.setString(2, "test_alice123");
                pstmt.setString(3, passwordEncoder.encode("1234"));
                pstmt.setString(4, "010-1111-2222");
                pstmt.setString(5, "alice@test.com");
                pstmt.setObject(6, LocalDateTime.now());
                pstmt.setObject(7, LocalDateTime.now());
                pstmt.setString(8, "국민은행");
                pstmt.setObject(9, LocalDate.of(1990, 1, 1));

                int result = pstmt.executeUpdate();
                log.info("User 데이터 삽입 완료: {} rows", result);

                // AUTO_INCREMENT로 생성된 user_id 가져오기
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        insertedUserId = rs.getInt(1);
                        log.info("생성된 user_id: {}", insertedUserId);
                    } else {
                        throw new RuntimeException("user_id를 가져올 수 없습니다");
                    }
                }
            }

            // 2. 생성된 user_id로 Auth 데이터 삽입
            if (insertedUserId > 0) {
                String authSql = "INSERT INTO Auth (user_id, auth) VALUES (?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(authSql)) {
                    // ROLE_USER 권한
                    pstmt.setInt(1, insertedUserId);
                    pstmt.setString(2, "ROLE_USER");
                    int userAuthResult = pstmt.executeUpdate();
                    log.info("ROLE_USER 권한 삽입: {} rows, user_id={}", userAuthResult, insertedUserId);

                    // ROLE_ADMIN 권한
                    pstmt.setInt(1, insertedUserId);
                    pstmt.setString(2, "ROLE_ADMIN");
                    int adminAuthResult = pstmt.executeUpdate();
                    log.info("ROLE_ADMIN 권한 삽입: {} rows, user_id={}", adminAuthResult, insertedUserId);
                }
            }

            return insertedUserId;
        }
    }

//    @Test
//    public void get_존재하지_않는_사용자() {
//        // Given - 존재하지 않는 user_id
//        int nonExistentUserId = 99999;
//
//        // When
//        UserVO user = mapper.get("test_alice1223");
//
//        // Then
//        assertNull(user, "존재하지 않는 사용자는 null이 반환되어야 함");
//        log.info("존재하지 않는 사용자 테스트 통과");
//    }
}

