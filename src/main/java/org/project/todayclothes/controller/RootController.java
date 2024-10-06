package org.project.todayclothes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;


@Controller
public class RootController {
    @GetMapping({"", "/"})
    public String root() {
        return "index.html";
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        return System.getenv();
    }
}

