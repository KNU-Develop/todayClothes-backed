package org.project.todayclothes.repository;

import org.project.todayclothes.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>{
}
