package org.project.todayclothes.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
        return jwtUtil.getCategory(refreshToken).equals(JWTUtil.REFRESH);
    }

    public String createAccessToken(String refreshToken) {
        return createToken(refreshToken, JWTUtil.ACCESS);
    }

    public String createRefreshToken(String refreshToken) {
        String newRefreshToken = createToken(refreshToken, JWTUtil.REFRESH);
        replaceRefreshToken(refreshToken, newRefreshToken);
        return newRefreshToken;
    }

    private String createToken(String refreshToken, String category) {
        String socialId = jwtUtil.getSocialId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        return jwtUtil.createJwt(category, socialId, role);
    }

    public void addRefreshEntity(String socialId, String refreshToken) {
        LocalDateTime dateTime = LocalDateTime.now().plus(refreshExpiredMs, ChronoUnit.MILLIS);
//        RefreshEntity refreshEntity = new RefreshEntity(socialId, refreshToken, dateTime);
        Optional<RefreshEntity> optRefreshEntity = refreshRepository.findBySocialId(socialId);
        if (optRefreshEntity.isPresent()) {
            RefreshEntity refreshEntity = optRefreshEntity.get();
            refreshEntity.update(refreshToken, dateTime);
            refreshRepository.save(refreshEntity);
        }else {
            refreshRepository.save(new RefreshEntity(socialId, refreshToken, dateTime));
        }
    }

    private void replaceRefreshToken(String refreshToken, String newRefreshToken) {
        refreshRepository.deleteByRefreshToken(refreshToken);
        addRefreshEntity(jwtUtil.getSocialId(newRefreshToken), newRefreshToken);
    }
}
