package com.kjung.batchtemplate.quartz.executor;

import com.kjung.batchtemplate.core.batch.BatchJobRunner;
import com.kjung.batchtemplate.quartz.registrar.QuartzBatchJobRegistrar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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

    private final BatchJobRunner batchJobRunner;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String jobName = jobDataMap.getString(QuartzBatchJobRegistrar.JOB_NAME);

            Map<String, Object> params = new HashMap<>(jobDataMap);
            params.remove(QuartzBatchJobRegistrar.JOB_NAME); // 파라미터만 추출

            batchJobRunner.run(jobName, params);

        } catch (Exception e) {
            throw new JobExecutionException("Batch job failed to execute", e);
        }
    }
}