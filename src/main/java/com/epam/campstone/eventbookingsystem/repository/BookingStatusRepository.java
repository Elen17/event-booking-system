package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingStatusRepository extends JpaRepository<BookingStatus, Long> {
    Optional<BookingStatus> findByName(String name);
    boolean existsByName(String name);
    List<BookingStatus> findByIsActive(boolean isActive);

    default BookingStatus findByNameOrThrow(String name) {
        return findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("Invalid booking status: " + name));
    }
}
