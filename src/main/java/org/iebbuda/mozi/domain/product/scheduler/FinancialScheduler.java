package org.iebbuda.mozi.domain.product.scheduler;

import lombok.extern.log4j.Log4j2;
import org.iebbuda.mozi.domain.product.service.DepositSyncService;
import org.iebbuda.mozi.domain.product.service.SavingSyncService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Log4j2
@Component
public class FinancialScheduler {

    private final DepositSyncService depositSyncService;
    private final SavingSyncService savingSyncService;

    public FinancialScheduler(DepositSyncService depositSyncService, SavingSyncService savingSyncService) {
        this.depositSyncService = depositSyncService;
        this.savingSyncService = savingSyncService;
    }

//    // 매일 새벽 2시 정기예금/적금 동기화 ->실제 사용시 코드
//    @Scheduled(cron = "0 0 2 * * *")
//    public void syncFinancialProducts() {
//        log.info("📡 금융감독원 정기예금 데이터 동기화 시작");
//        depositService.fetchAndSaveDeposits();
//        log.info("✅ 정기예금 동기화 완료");
//
//        log.info("📡 금융감독원 적금 데이터 동기화 시작");
//        savingService.fetchAndSaveSavings();
//        log.info("✅ 적금 동기화 완료");
//    }
    @PostConstruct
    public void init() {
        log.info("🚀 서버 시작 시 금융감독원 데이터 동기화 시작");

        // 정기예금 데이터 동기화
        depositSyncService.fetchAndSaveDeposits();
        log.info("✅ 정기예금 데이터 동기화 완료");

        // 적금 데이터 동기화
        savingSyncService.fetchAndSaveSavings();
        log.info("✅ 적금 데이터 동기화 완료");
    }
}
