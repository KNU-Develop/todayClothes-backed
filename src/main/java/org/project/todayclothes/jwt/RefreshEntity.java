package org.project.todayclothes.jwt;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class RefreshEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String socialId;
    private String refreshToken;
    private LocalDateTime expiresAt;

    public RefreshEntity(String socialId, String refreshToken, LocalDateTime expiresAt) {
        this.socialId = socialId;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }
    public void update(String refreshToken, LocalDateTime expiresAt) {
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }
}
