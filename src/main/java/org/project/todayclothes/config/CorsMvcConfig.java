package org.project.todayclothes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {
    @Value("${spring.jwt.allowed_redirect_uri_list}")
    private List<String> allowedRedirectUriList;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**")
                .allowedOrigins(allowedRedirectUriList.toArray(new String[0]));
    }
}
