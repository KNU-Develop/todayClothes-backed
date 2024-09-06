package org.project.todayclothes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HealthCheckController {
    @GetMapping({"/", "/env"})
    public String root() {
        return "index.html";
    }
}
