package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.EventStatus;
import com.epam.campstone.eventbookingsystem.model.EventType;
import com.epam.campstone.eventbookingsystem.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Find events by status with pagination.
     *
     * @param status   the status to filter by
     * @param pageable pagination and sorting information
     * @return a page of events with the given status
     */
    Page<Event> findByStatus(@Param("status") EventStatus status, Pageable pageable);

    /**
     * Find events by type with pagination.
     *
     * @param type     the event type to filter by
     * @param pageable pagination and sorting information
     * @return a page of events of the given type
     */
    Page<Event> findByType(@Param("type") EventType type, Pageable pageable);

    /**
     * Find events by venue with pagination.
     *
     * @param venue    the venue to filter by
     * @param pageable pagination and sorting information
     * @return a page of events at the given venue
     */
    Page<Event> findByVenue(@Param("venue") Venue venue, Pageable pageable);

    /**
     * Find events on a specific date.
     *
     * @param date the date to find events for
     * @return a list of events on the given date
     */
    List<Event> findByEventDate(@Param("date") LocalDate date);

    /**
     * Find events between two dates (inclusive).
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return a list of events between the given dates
     */
    List<Event> findByEventDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Find events after a specific date.
     *
     * @param date the date to find events after
     * @return a list of events after the given date
     */
    List<Event> findByEventDateAfter(@Param("date") LocalDate date);

    /**
     * Find events after a specific date with pagination.
     *
     * @param date     the date to find events after
     * @param pageable pagination and sorting information
     * @return a page of events after the given date
     */
    List<Event> findByEventDateAfter(@Param("date") LocalDate date, Pageable pageable);

    /**
     * Find events before a specific date.
     *
     * @param date the date to find events before
     * @return a list of events before the given date
     */
    List<Event> findByEventDateBefore(@Param("date") LocalDate date);

    /**
     * Find events by city ID with pagination.
     *
     * @param cityId   the ID of the city to filter by
     * @param pageable pagination and sorting information
     * @return a page of events in the given city
     */
    @Query("SELECT e FROM Event e JOIN e.venue v JOIN v.city c WHERE c.id = :cityId")
    Page<Event> findByCityId(@Param("cityId") Long cityId, Pageable pageable);

    /**
     * Find events by country ID with pagination.
     *
     * @param countryId the ID of the country to filter by
     * @param pageable  pagination and sorting information
     * @return a page of events in the given country
     */
    @Query("SELECT e FROM Event e JOIN e.venue v JOIN v.city c JOIN c.country co WHERE co.id = :countryId")
    Page<Event> findByCountryId(@Param("countryId") Long countryId, Pageable pageable);

    /**
     * Search events by title or description (case-insensitive).
     *
     * @param query    the search query
     * @param pageable pagination and sorting information
     * @return a page of events matching the search query
     */
    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(concat('%', :query, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(concat('%', :query, '%'))")
    Page<Event> searchEvents(@Param("query") String query, Pageable pageable);

    /**
     * Find all upcoming events (PLANNED status and event date in the future).
     * Results are ordered by event date and start time.
     *
     * @return a list of upcoming events
     */
    @Query("SELECT e FROM Event e WHERE e.status.name = 'PLANNED' AND e.eventDate >= CURRENT_DATE " +
            "ORDER BY e.eventDate, e.startTime")
    List<Event> findUpcomingEvents(Pageable pageable);
    /**
     * Update the status of an event.
     *
     * @param eventId the ID of the event to update
     * @param status  the new status
     * @return the number of updated records
     */
    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.status = :status WHERE e.id = :eventId")
    int updateStatus(Long eventId, EventStatus status);

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

    @Query("SELECT e FROM Event e WHERE e.eventDate >= CURRENT_DATE " +
            "AND e.created_by = :userId " +
            "ORDER BY e.eventDate, e.startTime")
    List<Event> findUpcomingEvents(Pageable pageable, Long userId);
}
