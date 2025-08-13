package org.iebbuda.mozi.config;


import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableScheduling
@Log4j2
public class AsyncConfig implements SchedulingConfigurer {

    /**
     * 회원 탈퇴 관련 백그라운드 작업용 Executor
     */
    @Bean("withdrawalTaskExecutor")
    public Executor withdrawalTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 기본 스레드 수
        executor.setCorePoolSize(2);

        // 최대 스레드 수
        executor.setMaxPoolSize(5);

        // 큐 용량
        executor.setQueueCapacity(100);

        // 스레드 이름 접두사
        executor.setThreadNamePrefix("withdrawal-task-");

        // 큐가 가득 찰 때 정책 (CallerRunsPolicy: 호출한 스레드에서 실행)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 애플리케이션 종료 시 대기 시간
        executor.setAwaitTerminationSeconds(60);

        // 완료되지 않은 작업을 기다릴지 여부
        executor.setWaitForTasksToCompleteOnShutdown(true);

        executor.initialize();

        log.info("withdrawalTaskExecutor 초기화 완료 - coreSize: {}, maxSize: {}, queueCapacity: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

        return executor;
    }

    /**
     * 일반적인 비동기 작업용 Executor (선택적)
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("async-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAwaitTerminationSeconds(30);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();

        log.info("taskExecutor 초기화 완료");
        return executor;
    }

    /**
     * 스케줄링 작업용 TaskScheduler 설정
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // 스케줄링 전용 스레드풀 설정
        scheduler.setPoolSize(3);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);

        scheduler.initialize();
        taskRegistrar.setTaskScheduler(scheduler);

        log.info("scheduledTaskExecutor 초기화 완료 - poolSize: {}", scheduler.getPoolSize());
    }
}
