package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventStatusRepository extends JpaRepository<EventStatus, Long> {
    Optional<EventStatus> findByName(String name);
    boolean existsByName(String name);

    default EventStatus findByNameOrThrow(String name) {
        return findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("Invalid event status: " + name));
    }
}
