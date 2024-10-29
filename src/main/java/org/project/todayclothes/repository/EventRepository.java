package org.project.todayclothes.repository;

import org.project.todayclothes.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByUserId(Long userId, Pageable pageable);
    Page<Event> findAll(Pageable pageable);
}
