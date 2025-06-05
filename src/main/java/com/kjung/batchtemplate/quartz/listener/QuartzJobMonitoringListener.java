package com.kjung.batchtemplate.quartz.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import java.util.Optional;

/**
 * Quartz 스케줄러의 Job 실행 과정을 모니터링하는 리스너 클래스입니다.
 *
 * <p>이 리스너는 Job의 실행 전후 이벤트를 감지하여, 다음과 같은 기능을 수행합니다:
 * <ul>
 *   <li>Job 실행 예정 로그 출력</li>
 *   <li>Job 실행 취소(veto) 감지</li>
 *   <li>Job 성공/실패 여부 로깅</li>
 *   <li>실행 시간 측정 및 저장</li>
 * </ul>
 *
 * <p>이 클래스를 Quartz Scheduler에 등록하면 모든 Job의 실행 상태를 중앙에서 추적할 수 있으며,
 * 운영 환경에서의 로그 분석, 지표 수집, 장애 감지 등에 유용하게 활용할 수 있습니다.
 *
 * @author 김정현
 */
@Slf4j
public class QuartzJobMonitoringListener implements JobListener {

    /** Job 시작 시간을 저장하기 위한 JobExecutionContext key */
    private static final String START_TIME_KEY = "startTime";

    /**
     * 리스너의 고유 이름을 반환합니다.
     * Quartz 내부에서 리스너를 식별할 때 사용됩니다.
     *
     * @return 리스너의 이름
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Job 실행 전 호출되며, 실행 예정 로그를 출력하고 시작 시간을 기록합니다.
     *
     * @param context 현재 실행될 Job의 컨텍스트
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().getName();
        log.info("[Quartz] Job '{}' 실행 예정", jobName);
        context.put(START_TIME_KEY, System.currentTimeMillis());
    }

    /**
     * Job 실행이 거부(veto)된 경우 호출되며, 실행 취소 로그를 출력합니다.
     *
     * @param context 실행이 취소된 Job의 컨텍스트
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().getName();
        log.warn("[Quartz] Job '{}' 실행이 거부됨", jobName);
    }

    /**
     * Job 실행 완료 후 호출되며, 성공/실패 여부와 실행 시간을 기록하고 후처리를 수행합니다.
     *
     * @param context     실행된 Job의 컨텍스트
     * @param jobException Job 실행 중 발생한 예외. 정상 완료된 경우 null
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        String jobName = context.getJobDetail().getKey().getName();

        long startTime = Optional.ofNullable(context.get(START_TIME_KEY))
                .map(Long.class::cast)
                .orElse(System.currentTimeMillis());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        if (jobException != null) {
            log.error("[Quartz] Job '{}' 실행 실패: {}", jobName, jobException.getMessage());
        } else {
            log.info("[Quartz] Job '{}' 실행 성공", jobName);
        }

        log.info("[Quartz] Job '{}' 실행시간: {} ms", jobName, duration);

        try {
            saveJobMetrics(jobName, duration, jobException == null);
        } catch (Exception e) {
            log.error("[Quartz] Job '{}' 실행 로그 저장 실패", jobName, e);
        }
    }

    /**
     * Job 실행 결과(성공 여부, 실행 시간)를 저장하거나 외부 시스템과 연동하는 메서드입니다.
     * 실제 구현에서는 DB, 모니터링 시스템(Prometheus 등)과 연동이 가능합니다.
     *
     * @param jobName       Job 이름
     * @param executionTime 실행 시간 (ms)
     * @param success       성공 여부
     */
    private void saveJobMetrics(String jobName, long executionTime, boolean success) {
        log.info("[Quartz] Job '{}' 결과 저장: success={}, time={}ms", jobName, success, executionTime);
    }
}
