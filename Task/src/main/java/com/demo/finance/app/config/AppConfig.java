package com.demo.finance.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code AppConfig} class is a configuration class that defines application-wide settings
 * for the Spring MVC framework. It enables CORS mappings, configures resource handlers for serving static content
 * (e.g., Swagger UI), and sets up view controllers. This class is annotated with {@link Configuration},
 * {@link EnableWebMvc}, and {@link EnableAspectJAutoProxy} to configure
 * Spring's web MVC capabilities and AspectJ-based proxying.
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"com.demo.finance", "org.springdoc"})
public class AppConfig implements WebMvcConfigurer {

    private static final Logger log = Logger.getLogger(AppConfig.class.getName());

    /**
     * Configures Cross-Origin Resource Sharing (CORS) mappings to allow requests from specific origins and methods.
     * <p>
     * This method permits requests from {@code http://localhost:8080} and allows the HTTP methods
     * GET, POST, PUT, DELETE, and OPTIONS. Credentials (e.g., cookies or authorization headers) are also allowed
     * for cross-origin requests.
     *
     * @param registry the {@link CorsRegistry} used to define CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // Allow requests only from localhost:8080
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Configures resource handlers to serve static resources such as Swagger UI, webjars, and API documentation.
     * <p>
     * This method maps the following paths:
     * <ul>
     *   <li>{@code /swagger-ui/**} to Swagger UI resources located in the classpath.</li>
     *   <li>{@code /webjars/**} to webjars resources located in the classpath.</li>
     *   <li>{@code /v3/api-docs/**} to OpenAPI documentation resources located in the classpath.</li>
     * </ul>
     * Resource chaining is disabled for development purposes to ensure resources are not cached.
     *
     * @param registry the {@link ResourceHandlerRegistry} used to define resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger UI
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/swagger-ui/")
                .resourceChain(false);

        // Webjars
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // API docs
        registry.addResourceHandler("/v3/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    /**
     * Configures view controllers to redirect specific paths to predefined views.
     * <p>
     * This method redirects the root path ({@code /}) to the Swagger UI index page ({@code /swagger-ui/index.html}),
     * providing a convenient entry point for API documentation.
     *
     * @param registry the {@link ViewControllerRegistry} used to define view controllers
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
    }

    /**
     * Configures custom exception handling for specific error scenarios.
     * <p>
     * This method adds a custom {@link HandlerExceptionResolver} to handle {@link HttpMessageNotReadableException},
     * which occurs when a request contains malformed JSON. If such an exception is caught, a structured JSON response
     * with an HTTP 400 (Bad Request) status is returned. Other exceptions are not handled by this resolver.
     * </p>
     *
     * @param resolvers the list of {@link HandlerExceptionResolver} instances to configure
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, (request, response, handler, e) -> {
            if (e instanceof HttpMessageNotReadableException) {
                try {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.getWriter().write(
                            "{\"error\":\"Invalid JSON format\",\"details\":\"Malformed request body\"}"
                    );
                    return new ModelAndView();
                } catch (IOException ex) {
                    log.log(Level.SEVERE, "Failed to write JSON error response", ex);
                    return null;
                }
            }
            return null;
        });
    }
}