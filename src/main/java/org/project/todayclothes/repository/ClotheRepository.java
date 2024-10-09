package org.project.todayclothes.repository;

import org.project.todayclothes.entity.Clothe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClotheRepository extends JpaRepository<Clothe, Long> {
}
