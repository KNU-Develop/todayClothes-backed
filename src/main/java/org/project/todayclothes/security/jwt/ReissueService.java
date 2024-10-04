package org.project.todayclothes.security.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

import static org.project.todayclothes.security.jwt.JWTUtil.REFRESH;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Value("${spring.jwt.refresh_expired_ms}")
    private Long refreshExpiredMs;
    private static final String REFRESH_COOKIE_NAME = "refresh";

    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName());
            if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            boolean tokenInWhiteList = refreshRepository.existsByRefreshToken(refreshToken);
            return !jwtUtil.isExpired(refreshToken) && tokenInWhiteList;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String refreshToken) {
        return jwtUtil.getCategory(refreshToken).equals(REFRESH);
    }

    public String createAccessToken(String refreshToken) {
        return createToken(refreshToken, JWTUtil.ACCESS);
    }

    public String createRefreshToken(String refreshToken) {
        String newRefreshToken = createToken(refreshToken, REFRESH);
        replaceRefreshToken(refreshToken, newRefreshToken);
        return newRefreshToken;
    }

    private String createToken(String refreshToken, String category) {
        String socialId = jwtUtil.getSocialId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        return jwtUtil.createJwt(category, socialId, role);
    }

    public void addRefreshEntity(String socialId, String refreshToken) {
        Date date = new Date(System.currentTimeMillis() + refreshExpiredMs);
        RefreshEntity refreshEntity = new RefreshEntity(socialId, refreshToken, date.toString());
        refreshRepository.save(refreshEntity);
    }

    private void replaceRefreshToken(String refreshToken, String newRefreshToken) {
        refreshRepository.deleteByRefreshToken(refreshToken);
        addRefreshEntity(jwtUtil.getSocialId(newRefreshToken), newRefreshToken);
    }
}
