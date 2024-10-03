package org.project.todayclothes.security.jwt;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    public final static String ACCESS = "ACCESS_TOKEN";
    public final static String REFRESH = "REFRESH_TOKEN";

    private final SecretKey secretKey;
    @Value("${spring.jwt.access_expired_ms}")
    private Long accessExpiredMs;

    @Value("${spring.jwt.refresh_expired_ms}")
    private Long refreshExpiredMs;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getSocialId(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("socialId", String.class);
    }
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }
    public String createJwt(String category, String socialId, String role) {
        long expiredMs = 0;
        if (category.equals(ACCESS)) {
            expiredMs = accessExpiredMs;
        } else if(category.equals(REFRESH)) {
            expiredMs = refreshExpiredMs;
        }

        return Jwts.builder()
                .claim("category", category)
                .claim("socialId", socialId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category").toString();
    }
    public Cookie createHttpOnlySecureCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh", refreshToken);
        cookie.setMaxAge((int) (refreshExpiredMs / 1000));
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
