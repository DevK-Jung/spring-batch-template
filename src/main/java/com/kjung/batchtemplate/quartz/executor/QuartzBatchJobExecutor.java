package com.kjung.batchtemplate.quartz.executor;

import com.kjung.batchtemplate.quartz.registrar.QuartzBatchJobRegistrar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Quartz에서 트리거된 작업을 통해 Spring Batch Job을 실행하는 Quartz Job 구현체입니다.
 *
 * <p>JobDataMap에서 Batch Job 이름과 파라미터를 추출하여, Scheduler Context에 등록된
 * Job 객체를 찾아 실행합니다.</p>
 * <p>
 * 이 클래스는 Quartz와 Spring Batch 간의 실행 연결(bridge) 역할을 수행합니다.
 *
 * @author 김정현
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzBatchJobExecutor implements Job {

    private final JobLauncher jobLauncher;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String jobName = jobDataMap.getString(QuartzBatchJobRegistrar.JOB_NAME);

            org.springframework.batch.core.Job job = findJobInSchedulerContext(context, jobName);
            JobParameters jobParameters = buildJobParameters(jobDataMap);

            jobLauncher.run(job, jobParameters);

        } catch (Exception e) {
            throw new JobExecutionException("Batch job failed to execute", e);
        }
    }

    /**
     * 스케줄러 컨텍스트에서 Job 이름에 해당하는 Spring Batch Job 객체를 조회합니다.
     */
    private org.springframework.batch.core.Job findJobInSchedulerContext(JobExecutionContext context, String jobName)
            throws SchedulerException {

        Object job = context.getScheduler().getContext().get(jobName);
        if (!(job instanceof org.springframework.batch.core.Job)) {
            throw new IllegalStateException("No valid Spring Batch Job found in scheduler context for name: " + jobName);
        }
        return (org.springframework.batch.core.Job) job;
    }

    /**
     * Quartz JobDataMap을 Spring Batch JobParameters로 변환합니다.
     */
    private JobParameters buildJobParameters(JobDataMap jobDataMap) {
        Map<String, Object> map = new HashMap<>(jobDataMap);
        map.remove(QuartzBatchJobRegistrar.JOB_NAME);

        map.put("uuid", UUID.randomUUID().toString()); // 재실행을 위한 고유 파라미터

        return getJobParametersFromJobMap(map);
    }

    private JobParameters getJobParametersFromJobMap(Map<String, Object> jobDataMap) {

        JobParametersBuilder builder = new JobParametersBuilder();

        for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                builder.addString(key, value.toString());
            } else if (value instanceof Float || value instanceof Double) {
                builder.addDouble(key, ((Number) value).doubleValue());
            } else if (value instanceof Integer || value instanceof Long) {
                builder.addLong(key, ((Number) value).longValue());
            } else if (value instanceof Date) {
                builder.addDate(key, (Date) value);
            } else if (value instanceof LinkedHashMap<?, ?> map) {
                builder.addJobParameter(key, new ArrayList<>(map.values()), List.class);
            } else if (value instanceof ArrayList<?> list) {
                builder.addJobParameter(key, list, List.class);
            } else {
                builder.addJobParameter(key, value, Object.class);
            }
        }

        return builder.toJobParameters();
    }
}