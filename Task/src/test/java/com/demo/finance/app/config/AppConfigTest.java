package com.demo.finance.app.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppConfigTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeAll
    static void setup() {
        System.setProperty("ENV_PATH", "src/test/resources/.env");
        System.setProperty("YML_PATH", "src/test/resources/application.yml");
    }

    @Test
    @DisplayName("Verify CORS allowed on authenticate endpoint")
    void testAuthenticateEndpointCors() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", "http://localhost:8080");
        headers.add("Access-Control-Request-Method", "POST");

        RequestEntity<Void> request = RequestEntity.options(getBaseUrl() + "/api/users/authenticate")
                .headers(headers).build();

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get("Access-Control-Allow-Origin")).contains("http://localhost:8080");
        assertThat(response.getHeaders().get("Access-Control-Allow-Methods")).contains("POST");
    }

    @Test
    @DisplayName("Verify Swagger UI is accessible")
    void testSwaggerUI() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(getBaseUrl() + "/swagger-ui/index.html", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Verify invalid JSON returns 400 Bad Request")
    void testMalformedJsonErrorHandling() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>("{ invalid json }", headers);
        ResponseEntity<String> response =
                restTemplate.postForEntity(getBaseUrl() + "/api/users/authenticate", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid JSON format");
    }
}