package org.project.todayclothes.repository;

import org.project.todayclothes.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>{
    List<Event> findAllByUserId(Long userId);
}
