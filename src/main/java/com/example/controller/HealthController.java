package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> m = new HashMap<>();
        m.put("status", "ok");
        m.put("timestamp", Instant.now().toString());
        m.put("app", "AIPoweredStudentResultAnalyzer");
        return ResponseEntity.ok(m);
    }
}
