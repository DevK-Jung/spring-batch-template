// QuartzService.java
package com.kjung.batchtemplate.quartz.registrar;

import com.kjung.batchtemplate.core.property.QuartzJobProperties;
import com.kjung.batchtemplate.quartz.executor.QuartzBatchJobExecutor;
import com.kjung.batchtemplate.quartz.listener.QuartzJobMonitoringListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;


/**
 * Quartz 스케줄러에 등록 설정된 Spring Batch Job들을 동적으로 등록하는 컴포넌트입니다.
 *
 * <p>설정 파일(schedule.yml 등)에 정의된 Job 중 등록 플래그(registered=true)가 설정된 Job만을 대상으로 등록하며,
 * 동일한 JobKey가 존재할 경우 삭제 후 재등록합니다. Quartz의 JobDataMap을 통해 BatchLauncher로 Job 이름과 파라미터를 전달합니다.</p>
 *
 * <p>등록된 Job은 {@link QuartzBatchJobExecutor}를 통해 실행됩니다.</p>
 *
 * @author 김정현
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.enabled", havingValue = "false")
//@ConditionalOnProperty(name = "batch.quartz.enabled", havingValue = "true")
public class QuartzBatchJobRegistrar {

    /**
     * Quartz JobDataMap에 전달되는 Spring Batch Job 식별 키
     */
    public static final String JOB_NAME = "JOB_NAME";

    /**
     * Job과 Trigger의 그룹명
     */
    private static final String BATCH_GROUP = "batch-jobs";
    private static final String TRIGGER_GROUP = "trigger-jobs";

    private final Scheduler scheduler;

    private final QuartzJobProperties quartzJobProperties;

    /**
     * 애플리케이션 초기화 시점에 Quartz Job 등록을 수행합니다.
     * 설정된 Job들 중 registered=true인 Job만 대상으로 등록합니다.
     */
    @PostConstruct
    public void init() {
        try {

            scheduler.getListenerManager().addJobListener(new QuartzJobMonitoringListener());

            List<QuartzJobProperties.JobDetailProperties> activeJobs = getRegisteredJobs();

            int successCount = 0;
            int failureCount = 0;

            for (QuartzJobProperties.JobDetailProperties job : activeJobs) {
                try {
                    registerJob(job);
                    successCount++;
                } catch (SchedulerException e) {
                    failureCount++;
                    log.error("Failed to register job: {}", job.getName(), e);
                }
            }

            if (activeJobs.size() == failureCount)
                throw new IllegalStateException("All Quartz jobs failed to register. Check configuration or job definitions.");

            log.warn("Quartz job registration completed with {} success / {} failure(s).", successCount, failureCount);

        } catch (SchedulerException e) {
            log.error("Quartz initialization failed", e);
            throw new IllegalStateException("Quartz initialization failed", e); // 반드시 전체 실패인 경우 예외 던짐
        }
    }

    /**
     * 등록 대상 Job 목록을 필터링합니다.
     */
    private List<QuartzJobProperties.JobDetailProperties> getRegisteredJobs() {
        return quartzJobProperties.getJobs().stream()
                .filter(QuartzJobProperties.JobDetailProperties::isRegistered)
                .toList();
    }

    /**
     * 주어진 Job 정보를 기반으로 Quartz JobDetail 및 Trigger를 생성하고 스케줄러에 등록합니다.
     */
    private void registerJob(QuartzJobProperties.JobDetailProperties job) throws SchedulerException {
        String name = job.getName();
        String cron = job.getCron();
        Map<String, Object> params = job.getParams();

        JobDetail jobDetail = buildJobDetail(name, cron, params);

        Trigger trigger = buildCronTrigger(name, cron);

        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
            log.info("Deleted existing job: {}", name);
        }

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Scheduled job: {} with cron: {}", name, cron);
    }

    /**
     * Spring Batch Job 실행을 위임할 Quartz JobDetail을 생성합니다.
     */
    private JobDetail buildJobDetail(String name, String description, Map<String, Object> paramsMap) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_NAME, name);

        if (paramsMap != null && !paramsMap.isEmpty()) jobDataMap.putAll(paramsMap);

        return JobBuilder.newJob(QuartzBatchJobExecutor.class)
                .withIdentity(name, BATCH_GROUP)
                .withDescription(description)
                .usingJobData(jobDataMap)
                .build();
    }

    /**
     * 주어진 cron 표현식으로 Quartz Trigger를 생성합니다.
     */
    private Trigger buildCronTrigger(String jobName, String cronExp) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobName + "_trigger", TRIGGER_GROUP) // 트리거 ID 설정
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();
    }
}