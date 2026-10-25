package com.trixit.infrastructure.web;

import com.trixit.application.CheckHealthUseCase;
import com.trixit.domain.HealthStatus;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final CheckHealthUseCase checkHealth;

    public HealthController(CheckHealthUseCase checkHealth) {
        this.checkHealth = checkHealth;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        HealthStatus status = checkHealth.execute();
        Map<String, String> body = Map.of("status", status.name());
        return status == HealthStatus.UP
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(503).body(body);
    }
}
