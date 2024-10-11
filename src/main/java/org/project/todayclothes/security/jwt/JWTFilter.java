package org.project.todayclothes.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.oauth2.CustomOAuth2User;
import org.project.todayclothes.dto.oauth2.Oauth2UserDto;
import org.project.todayclothes.exception.code.CommonErrorCode;
import org.project.todayclothes.util.ApiResponseUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;


@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    private final Set<String> permitAllPaths = Set.of(
            "/", "/env", "/login", "/index.html", "/docs/index.html", "/reissue", "/oauth2/*", "/login/oauth2/code/*"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getJwtFromAuthorizationHeader(request);

        if (permitAllPaths.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (accessToken == null) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isExpired(accessToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals(JWTUtil.ACCESS)) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String socialId = jwtUtil.getSocialId(accessToken);
        String role = jwtUtil.getRole(accessToken);
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(new Oauth2UserDto(socialId, role)));
        filterChain.doFilter(request, response);
    }

    private String getJwtFromAuthorizationHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 부분을 제거하고 JWT만 반환
        }
        return null;
    }

    private Authentication createAuthentication(Oauth2UserDto oAuth2UserDto) {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2UserDto);
        return new UsernamePasswordAuthenticationToken(
                customOAuth2User,
                null,
                customOAuth2User.getAuthorities()
        );
    }
}
