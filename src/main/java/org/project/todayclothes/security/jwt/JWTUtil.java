package org.project.todayclothes.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
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

    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;
    @Value("${spring.jwt.access_expired_ms}")
    private Long accessExpiredMs;

    @Value("${spring.jwt.refresh_expired_ms}")
    private Long refreshExpiredMs;

    public JWTUtil(@Value("${spring.jwt.secret_access}") String accessKey, @Value("${spring.jwt.secret_refresh}") String refreshKey) {
        this.accessSecretKey = new SecretKeySpec(accessKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshSecretKey = new SecretKeySpec(refreshKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getSocialId(String token) {
        return Jwts.parser().verifyWith(getSecretKey(token)).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("socialId", String.class);
    }
    public String getRole(String token) {
        return Jwts.parser().verifyWith(getSecretKey(token)).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(getSecretKey(token)).build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }
    public String createJwt(String category, String socialId, String role) {
        long expiredMs = -1;
        SecretKey secretKey = null;
        if (category.equals(ACCESS)) {
            expiredMs = accessExpiredMs;
            secretKey = accessSecretKey;
        } else if(category.equals(REFRESH)) {
            expiredMs = refreshExpiredMs;
            secretKey = refreshSecretKey;
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
        try {
            return Jwts.parser().verifyWith(accessSecretKey).build().parseSignedClaims(token).getPayload().get("category").toString();
        } catch (SignatureException e) {
            return Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token).getPayload().get("category").toString();
        }
    }
    public Cookie createHttpOnlySecureCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh", refreshToken);
        cookie.setMaxAge((int) (refreshExpiredMs / 1000));
        // cookie.setSecure(true);
        cookie.setPath("/");
        // cookie.setHttpOnly(true);
        return cookie;
    }
    private SecretKey getSecretKey(String token){
        return getCategory(token).equals(ACCESS) ? accessSecretKey : refreshSecretKey;
    }
}
