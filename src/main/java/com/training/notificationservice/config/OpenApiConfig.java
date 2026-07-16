package com.training.notificationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers API metadata for the springdoc-openapi UI, served at
 * {@code /swagger-ui.html} once the app is running.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notificationServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Notification Service API")
                .description("REST API for creating, retrieving, and searching notifications "
                        + "in the Mini E-Commerce Platform.")
                .version("v1.0"));
    }
}
