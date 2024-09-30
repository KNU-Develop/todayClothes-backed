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
    private static Long accessExpiredMs;

    @Value("${spring.jwt.refresh_expired_ms}")
    private static Long refreshExpiredMs;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", String.class);
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

    public String createJwt(CATEGORY category, String userId, String role) {
        Long expiredMs = switch (category){
            case ACCESS -> accessExpiredMs;
            case REFRESH -> refreshExpiredMs;
        };
        return Jwts.builder()
                .claim("category", category)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }
}
