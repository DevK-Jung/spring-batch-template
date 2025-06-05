package com.kjung.batchtemplate.core.config;

import com.kjung.batchtemplate.core.factory.YamlPropertySourceFactory;
import com.kjung.batchtemplate.core.property.QuartzJobProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * Quartz 관련 설정 프로퍼티를 외부 YAML 파일(schedule.yml)에서 로드하여
 * {@link QuartzJobProperties} 클래스에 바인딩하는 설정 클래스입니다.
 *
 * <p>기본적으로 {@code @PropertySource}는 properties 파일만 지원하지만,
 * {@code YamlPropertySourceFactory}를 통해 YAML 포맷도 지원하도록 확장하였습니다.</p>
 *
 * @author 김정현
 */
@Configuration
@PropertySource(
        name = "schedule.yaml",
        value = "classpath:schedule.yml",
        factory = YamlPropertySourceFactory.class
)
@EnableConfigurationProperties(QuartzJobProperties.class)
public class QuartzPropertiesConfig {

}
