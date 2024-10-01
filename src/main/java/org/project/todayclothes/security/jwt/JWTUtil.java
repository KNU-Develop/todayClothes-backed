package org.project.todayclothes.security.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    public enum CATEGORY {ACCESS, REFRESH};

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
        return Jwts.parser().verifyWith(secretKey).build().
                parseSignedClaims(token).
                getPayload().
                getExpiration().before(new Date());
    }

    public String createJwt(CATEGORY category, String socialId, String role) {
        Long expiredMs = switch (category) {
            case ACCESS -> accessExpiredMs;
            case REFRESH -> refreshExpiredMs;
        };
        return Jwts.builder()
                .claim("category", category)
                .claim("socialId", socialId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
    public JWTUtil.CATEGORY getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", JWTUtil.CATEGORY.class);
    }
}
