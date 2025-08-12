package org.iebbuda.mozi.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.user.mapper.WithdrawalMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class WithdrawalBatchService {

    private final WithdrawalMapper withdrawalMapper;

    /**
     * 만료된 백업 데이터 자동 정리
     * 매일 새벽 2시에 실행
     */
    @Scheduled(cron = "0 0 6 * * MON")
    @Transactional
    public void cleanupExpiredBackups() {
        log.info("만료된 탈퇴 사용자 백업 데이터 정리 시작");

        try {
            // 만료된 백업 데이터 삭제
            withdrawalMapper.findExpiredUsers();

            log.info("만료된 탈퇴 사용자 백업 데이터 정리 완료");

        } catch (Exception e) {
            log.error("만료된 백업 데이터 정리 실패", e);

            // TODO: 실패 시 관리자 알림
            // notificationService.sendAdminAlert("백업 데이터 정리 실패", e);
        }
    }
}
