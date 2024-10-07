package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByUserId(Long userId);

    // JpaRepository already provides findAll(), so no need to declare it here
}
