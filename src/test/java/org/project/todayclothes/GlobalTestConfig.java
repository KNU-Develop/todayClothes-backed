package org.project.todayclothes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(SecurityConfig.class)
public abstract class GlobalTestConfig {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private CustomOauth2AuthenticationSuccessHandler customOauth2AuthenticationSuccessHandler;

    @MockBean
    private CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver;

    @MockBean
    private JWTUtil jwtUtil;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) { // RestDocumentationContextProvider로 수정
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .alwaysDo(MockMvcRestDocumentation.document("{class-name}/{method-name}")) // 모든 테스트에 문서화 적용
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
                .build();
    }
}
