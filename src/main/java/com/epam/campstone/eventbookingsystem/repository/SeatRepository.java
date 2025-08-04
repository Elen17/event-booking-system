package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.Seat;
import com.epam.campstone.eventbookingsystem.model.SeatStatus;
import com.epam.campstone.eventbookingsystem.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByVenue(Venue venue);
    List<Seat> findByVenueId(Long venueId);
    List<Seat> findByStatus(SeatStatus status);

    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section")
    List<Seat> findByVenueAndSection(@Param("venue") Venue venue, @Param("section") String section);

    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.rowNumber = :rowNumber")
    List<Seat> findByVenueAndRow(@Param("venue") Venue venue, @Param("rowNumber") int rowNumber);

    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section AND s.rowNumber = :rowNumber")
    List<Seat> findByVenueSectionAndRow(
        @Param("venue") Venue venue,
        @Param("section") String section,
        @Param("rowNumber") int rowNumber
    );

    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section AND s.seatNumber = :seatNumber")
    Optional<Seat> findByVenueSectionAndSeatNumber(
        @Param("venue") Venue venue,
        @Param("section") String section,
        @Param("seatNumber") int seatNumber
    );

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.venue = :venue AND s.status = 'AVAILABLE'")
    int countAvailableSeatsByVenue(@Param("venue") Venue venue);

    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.status = 'AVAILABLE'")
    List<Seat> findAvailableSeatsByVenue(@Param("venue") Venue venue);

    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.status = 'AVAILABLE' AND s.section = :section")
    List<Seat> findAvailableSeatsByVenueAndSection(
        @Param("venue") Venue venue,
        @Param("section") String section
    );

    @Query("SELECT DISTINCT s.section FROM Seat s WHERE s.venue = :venue ORDER BY s.section")
    List<String> findSectionsByVenue(@Param("venue") Venue venue);

    @Query("SELECT DISTINCT s.rowNumber FROM Seat s WHERE s.venue = :venue AND s.section = :section ORDER BY s.rowNumber")
    List<Integer> findRowNumbersByVenueAndSection(
        @Param("venue") Venue venue,
        @Param("section") String section
    );

    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section AND s.rowNumber = :rowNumber AND s.seatNumber = :seatNumber")
    Optional<Seat> findByVenueAndSeatDetails(
        @Param("venue") Venue venue,
        @Param("section") String section,
        @Param("rowNumber") int rowNumber,
        @Param("seatNumber") int seatNumber
    );
}
