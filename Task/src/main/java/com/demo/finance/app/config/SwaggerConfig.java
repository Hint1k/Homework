package com.demo.finance.app.config;

import com.demo.finance.in.controller.UserController;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The {@code SwaggerConfig} class is a configuration class that sets up Swagger/OpenAPI documentation
 * for the application. It defines the API information, groups endpoints, and registers specific controllers
 * to be included in the generated API documentation.
 * <p>
 * This class uses SpringDoc to configure OpenAPI documentation and provides a customizable setup
 * for organizing and describing the API endpoints.
 */
@Configuration
public class SwaggerConfig {

    static {
        SpringDocUtils.getConfig().addRestControllers(UserController.class);
    }

    /**
     * Configures and customizes the OpenAPI documentation for the application.
     * <p>
     * This method defines metadata such as the title, version, and description of the API,
     * which will appear in the generated API documentation.
     *
     * @return an instance of {@link OpenAPI} containing the customized API information
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("My API")
                .version("1.0")
                .description("This is a sample API documentation"));
    }

    /**
     * Configures a grouped OpenAPI definition for public API endpoints.
     * <p>
     * This method specifies which endpoint paths should be included in the "public" group
     * of the API documentation. Only paths matching the pattern {@code /api/**} are included.
     *
     * @return an instance of {@link GroupedOpenApi} representing the grouped API definition
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}