package org.project.todayclothes.security.jwt;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String socialId;
    private String refreshToken;
    private String expiresAt;

    public RefreshEntity(String socialId, String refreshToken, String expiresAt) {
        this.socialId = socialId;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }
}
