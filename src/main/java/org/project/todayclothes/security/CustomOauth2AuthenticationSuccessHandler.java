package org.project.todayclothes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.oauth2.CustomOAuth2User;
import org.project.todayclothes.security.jwt.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String socialId = customOAuth2User.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createJwt(JWTUtil.CATEGORY.ACCESS, socialId, role);
        String refreshToken = jwtUtil.createJwt(JWTUtil.CATEGORY.REFRESH, socialId, role);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createHttpOnlySecureCookie(refreshToken));
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect(getRedirectUrl(request));
    }

    private Cookie createHttpOnlySecureCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh", refreshToken);
        cookie.setMaxAge(7 * 24 * 60 * 60); // = refresh_expired_ms
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    private String getRedirectUrl(HttpServletRequest request) {
        System.out.println(request.getSession().getAttribute("redirect_uri").toString());
        return request.getSession().getAttribute("redirect_uri").toString();
    }
}
