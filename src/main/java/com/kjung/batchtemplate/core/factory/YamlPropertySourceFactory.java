package com.kjung.batchtemplate.core.factory;

import lombok.NonNull;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

/**
 * Spring {@code @PropertySource}에서 YAML 파일을 읽을 수 있도록 지원하는 커스텀 팩토리 클래스입니다.
 *
 * <p>Spring 기본 PropertySource는 .properties 파일만 처리 가능하므로,
 * {@link YamlPropertiesFactoryBean}을 활용하여 YAML 파일을 {@link Properties} 객체로 변환하고
 * {@link PropertiesPropertySource} 형태로 반환합니다.</p>
 * <p>
 * 사용 예:
 * <pre>
 * @PropertySource(value = "classpath:schedule.yml", factory = YamlPropertySourceFactory.class)
 * </pre>
 *
 * @author 김정현
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@NonNull String name, EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());

        Properties properties = factory.getObject();

        return new PropertiesPropertySource(name, properties);
    }
}
