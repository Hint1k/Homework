package com.demo.finance.in.controller;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

/**
 * The {@code SwaggerController} class is a REST controller that provides an endpoint for retrieving API documentation
 * in the OpenAPI format. It allows clients to access the API specification programmatically.
 * <p>
 * This controller is mapped to the "/v3" base path and exposes the API documentation via the "/api-docs" endpoint.
 */
@RestController
@RequestMapping("/v3")
public class SwaggerController {

    private final OpenAPI openAPI;

    /**
     * Constructs a new {@code SwaggerController} instance with the required dependency
     * for providing OpenAPI documentation.
     *
     * @param openAPI the {@link OpenAPI} object containing the API specification
     */
    @Autowired
    public SwaggerController(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    /**
     * Retrieves the OpenAPI documentation for the application.
     * <p>
     * This endpoint returns the API specification in the OpenAPI format, which can be used by tools like Swagger UI
     * to visualize and interact with the API.
     *
     * @return a {@link ResponseEntity} containing the {@link OpenAPI} object representing the API documentation
     */
    @GetMapping("/api-docs")
    public ResponseEntity<OpenAPI> getApiDocs() {
        return ResponseEntity.ok(openAPI);
    }
}