package org.project.todayclothes.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
    Boolean existsByRefreshToken(String refresh);

    @Transactional
    void deleteByRefreshToken(String refreshToken);
}
