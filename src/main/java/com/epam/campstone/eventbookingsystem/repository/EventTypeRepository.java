package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long> {
    Optional<EventType> findByName(String name);
    boolean existsByName(String name);

    default EventType findByNameOrThrow(String name) {
        return findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("Invalid event type: " + name));
    }
}
