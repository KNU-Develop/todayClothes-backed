package org.project.todayclothes.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.project.todayclothes.repository.UserRepository;
import org.project.todayclothes.security.CustomOAuth2AuthorizationRequestResolver;
import org.project.todayclothes.security.CustomOauth2AuthenticationSuccessHandler;
import org.project.todayclothes.security.jwt.JWTFilter;
import org.project.todayclothes.security.jwt.JWTUtil;
import org.project.todayclothes.security.CustomOAuth2UserService;
import org.project.todayclothes.security.jwt.RefreshRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Configuration
//@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOauth2AuthenticationSuccessHandler customOauth2AuthenticationSuccessHandler;
    private final JWTUtil jwtUtil;
    @Value("${spring.jwt.allowed_redirect_uri_list}")
    private List<String> allowedRedirectUriList;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver) throws Exception {
        /*
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();
//                        configuration.setAllowedOrigins(allowedRedirectUriList);
                        configuration.setAllowedOrigins(Collections.singletonList("*"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
//                        configuration.setAllowCredentials(true);
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                        return configuration;
                    }
                }));
        */
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/env","/login").permitAll()
                        .requestMatchers("index.html","/docs/index.html").permitAll()
                        .requestMatchers("/event/**").authenticated()
                        .requestMatchers("/clothes/**").authenticated()
                        .requestMatchers("/gpt").permitAll()
                        .requestMatchers("/reissue").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login((oauth2) -> oauth2
                        .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.authorizationRequestResolver(customOAuth2AuthorizationRequestResolver)
                        )
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customOauth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
