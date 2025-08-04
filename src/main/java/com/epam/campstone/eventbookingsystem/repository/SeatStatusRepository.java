package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatStatusRepository extends JpaRepository<SeatStatus, Long> {
    Optional<SeatStatus> findByName(String name);
    boolean existsByName(String name);

    default SeatStatus findByNameOrThrow(String name) {
        return findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("Invalid seat status: " + name));
    }
}
