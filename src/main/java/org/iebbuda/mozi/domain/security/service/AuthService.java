package org.iebbuda.mozi.domain.security.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.security.account.mapper.AuthMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthMapper authMapper;


    /**
     * 사용자의 모든 권한 정보 삭제
     */
    @Transactional
    public void deleteAllAuthByUserId(int userId) {

            log.info("사용자 권한 데이터 삭제 시작 - userId: {}", userId);
            authMapper.deleteAllAuthByUserId(userId);
            log.info("사용자 권한 데이터 삭제 완료 - userId: {}", userId);

        }
    }

