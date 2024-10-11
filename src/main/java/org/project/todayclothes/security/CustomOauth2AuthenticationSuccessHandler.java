package org.project.todayclothes.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.oauth2.CustomOAuth2User;
import org.project.todayclothes.security.jwt.JWTUtil;
import org.project.todayclothes.security.jwt.ReissueService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

import static org.project.todayclothes.security.jwt.JWTUtil.ACCESS;
import static org.project.todayclothes.security.jwt.JWTUtil.REFRESH;

@Component
@RequiredArgsConstructor
public class CustomOauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    private final ReissueService reissueService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println(request.getHeader("Referer"));

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String socialId = customOAuth2User.getSocialId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createJwt(ACCESS, socialId, role);
        String refreshToken = jwtUtil.createJwt(REFRESH, socialId, role);
        reissueService.addRefreshEntity(socialId, refreshToken);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(jwtUtil.createHttpOnlySecureCookie(refreshToken));
        response.setStatus(HttpStatus.OK.value());
        String redirectUrl = String.format("%s?accessToken=%s&refreshToken=%s",
                getRedirectUrl(request),
                URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));
        response.sendRedirect(redirectUrl);
    }

    private String getRedirectUrl(HttpServletRequest request) {
//        System.out.println(request.getSession().getAttribute("redirect_uri").toString());
        return request.getSession().getAttribute("redirect_uri").toString();
    }
}
