package org.project.todayclothes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthCheckController {
    @GetMapping({"/", "/env"})
    public ResponseEntity<?> getEnv() {
        return ResponseEntity.ok("<h1>Today Clothes Backend Server</h1>");
    }
}
