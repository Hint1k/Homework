package com.demo.finance.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

/**
 * OpenAPI configuration for Swagger UI customization and JWT security support.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI documentation for the application.
     * <p>
     * This method creates an {@link OpenAPI} instance that includes the API title, version,
     * and security configurations, specifically for Bearer authentication using JWT.
     * </p>
     *
     * @return An {@link OpenAPI} instance with the custom configuration for API documentation.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Finance Tracker")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}