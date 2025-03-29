package com.demo.finance.in.controller;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class SwaggerController {

    @GetMapping
    public OpenAPI getApiDocs() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finance API")
                        .version("1.0")
                        .description("Finance Application API Documentation"))
                .addServersItem(new Server().url("/"));
    }
}