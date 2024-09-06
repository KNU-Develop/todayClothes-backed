package org.project.todayclothes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiDocsController {

    @GetMapping("/docs")
    public String getApiDocs() {
        return "docs/index";
    }
}