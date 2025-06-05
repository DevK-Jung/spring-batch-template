package com.kjung.batchtemplate.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * schedule.yml에 정의된 Quartz Job 설정을 매핑하기 위한 프로퍼티 클래스입니다.
 *
 * <p>prefix: {@code spring.quartz.jobs} 아래의 정보를 읽어와 Job 목록을 구성하며,
 * 각 Job의 이름, 설명, cron 주기, 등록 여부, 전달 파라미터 등을 포함합니다.</p>
 * <p>
 * 예시 YAML 구조:
 * <pre>
 * spring:
 *   quartz:
 *     jobs:
 *       - name: jobA
 *         description: "테스트 작업"
 *         cron: "0 0/5 * * * ?"
 *         registered: true
 *         params:
 *           key1: value1
 * </pre>
 *
 * @author 김정현
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.quartz")
public class QuartzJobProperties {

    // Quartz Job 목록
    private List<JobDetailProperties> jobs;

    @Data
    public static class JobDetailProperties {
        private String name; // 작업 이름
        private String description; // 작업 설명
        private String cron; // 작업 실행 주기(cron 표현식)
        private boolean registered; // Job 등록할지 여부
        private Map<String, Object> params; // Job 실행 시 전달할 파라미터
    }
}
