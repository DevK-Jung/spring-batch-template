package com.kjung.batchtemplate.core.swagger.config;

import com.kjung.batchtemplate.core.swagger.property.SwaggerInfoProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {

    @Bean
    @ConditionalOnBean(SwaggerInfoProperties.class)
    public OpenAPI customOpenAPI(SwaggerInfoProperties properties) {

        OpenAPI openAPI = createCustomOpenAPI(properties);

        if (Boolean.TRUE.equals(properties.getJwtHeaderEnabled())) {
            openAPI.components(new Components()
                            .addSecuritySchemes(HttpHeaders.AUTHORIZATION,
                                    new SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                                            .in(SecurityScheme.In.HEADER)
                                            .name(HttpHeaders.AUTHORIZATION)))
                    .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
        }

        return openAPI;
    }

    private OpenAPI createCustomOpenAPI(SwaggerInfoProperties properties) {
        Info info = new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion());

        // Contact 정보 추가
        SwaggerInfoProperties.Contact contact = properties.getContact();

        if (contact != null) {
            info.setContact(
                    new Contact()
                            .name(contact.getName())
                            .email(contact.getEmail())
                            .url(contact.getUrl())
            );
        }

        // License 정보 추가
        SwaggerInfoProperties.License license = properties.getLicense();

        if (license != null) {
            info.setLicense(
                    new License()
                            .name(license.getName())
                            .url(license.getUrl())
            );
        }

        return new OpenAPI().info(info);
    }
}
