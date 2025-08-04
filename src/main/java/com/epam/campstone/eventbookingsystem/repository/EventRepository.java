package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.EventStatus;
import com.epam.campstone.eventbookingsystem.model.EventType;
import com.epam.campstone.eventbookingsystem.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByStatus(EventStatus status, Pageable pageable);
    Page<Event> findByType(EventType type, Pageable pageable);
    Page<Event> findByVenue(Venue venue, Pageable pageable);
    List<Event> findByEventDate(LocalDate date);
    List<Event> findByEventDateBetween(LocalDate startDate, LocalDate endDate);
    List<Event> findByEventDateAfter(LocalDate date);
    List<Event> findByEventDateBefore(LocalDate date);
    
    @Query("SELECT e FROM Event e WHERE e.venue.city.id = :cityId")
    Page<Event> findByCityId(@Param("cityId") Long cityId, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.venue.city.country.id = :countryId")
    Page<Event> findByCountryId(@Param("countryId") Long countryId, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(concat('%', :query, '%')) " +
           "OR LOWER(e.description) LIKE LOWER(concat('%', :query, '%'))")
    Page<Event> searchEvents(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.status = 'PLANNED' AND e.eventDate >= CURRENT_DATE " +
           "ORDER BY e.eventDate, e.startTime")
    List<Event> findUpcomingEvents();
    
    @Query("SELECT e FROM Event e WHERE e.venue = :venue AND e.eventDate >= :startDate AND e.eventDate <= :endDate")
    List<Event> findEventsByVenueAndDateRange(
        @Param("venue") Venue venue,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT e FROM Event e WHERE e.venue = :venue AND e.eventDate = :date " +
           "AND ((e.startTime <= :endTime AND e.endTime >= :startTime) " +
           "OR (e.startTime <= :endTime AND e.endTime >= :startTime))")
    List<Event> findConflictingEvents(
        @Param("venue") Venue venue,
        @Param("date") LocalDate date,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
