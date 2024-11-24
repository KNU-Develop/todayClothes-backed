package org.project.todayclothes.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.project.todayclothes.GlobalTestConfig;
import org.project.todayclothes.config.SecurityConfig;
import org.project.todayclothes.jwt.JWTUtil;
import org.project.todayclothes.security.CustomOAuth2AuthorizationRequestResolver;
import org.project.todayclothes.security.CustomOAuth2UserService;
import org.project.todayclothes.security.CustomOauth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = RootController.class)
public class RootControllerTest extends GlobalTestConfig {

    @Autowired
    private WebApplicationContext context;

    @ExtendWith(SpringExtension.class)
    @Test
    @DisplayName("루트 경로 접속 테스트 및 REST Docs 생성")
    void testRootEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                .andExpect(view().name("index.html")); // 반환된 뷰 이름이 index.html인지 확인
    }
}
