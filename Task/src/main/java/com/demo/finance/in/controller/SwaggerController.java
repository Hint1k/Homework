package com.demo.finance.in.controller;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/v3")
public class SwaggerController {

    private final OpenAPI openAPI;

    @Autowired
    public SwaggerController(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    @GetMapping("/api-docs")
    public ResponseEntity<OpenAPI> getApiDocs() {
        return ResponseEntity.ok(openAPI);
    }
}