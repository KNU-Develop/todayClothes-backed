package org.project.todayclothes.repository;

import org.project.todayclothes.entity.Clothe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClotheRepository extends JpaRepository<Clothe, Long> {
    boolean existsByImgUrl(String imgUrl);
    Optional<Clothe> findByImgUrl(String imgUrl);
}
