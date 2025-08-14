package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for {@link Event} entities.
 * Provides methods to interact with event data in the database.
 */
@Repository
@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * Find all upcoming events (PLANNED status and event date in the future).
     * Results are ordered by event date and start time.
     *
     * @return a list of upcoming events
     */
    @Query(value = """
            SELECT *
            FROM event e
            WHERE (e.event_date + e.start_time) >= CURRENT_TIMESTAMP
            AND e.status_id = 1
            ORDER BY e.event_date, e.start_time
            """, nativeQuery = true)
    List<Event> findUpcomingEvents(Pageable pageable);

    /**
     * Search events by start date, event type, and city.
     *
     * @param startDate the start date
     * @param eventType the event type
     * @param city      the city
     * @param pageable  pagination and sorting information
     * @return a page of events matching the search criteria
     */
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :startDate " +
            "AND e.type.name = :eventType " +
            "AND e.venue.city.name = :city " +
            "ORDER BY e.eventDate, e.startTime")
    Page<Event> searchEvents(LocalDate startDate, String eventType, String city, Pageable pageable);

    @Query(value = """
            SELECT *
            FROM event e
            WHERE (e.event_date + e.start_time) >= CURRENT_TIMESTAMP
              AND e.created_by = :userId
            ORDER BY e.event_date, e.start_time
            """,
            nativeQuery = true)
    List<Event> findUpcomingEvents(Pageable pageable, Long userId);
}
