package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.Seat;
import com.epam.campstone.eventbookingsystem.model.SeatStatus;
import com.epam.campstone.eventbookingsystem.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Seat entities.
 * Provides methods for querying seats with various filtering options and pagination support.
 */
@Repository
@Transactional(readOnly = true)
public interface SeatRepository extends JpaRepository<Seat, Long> {
    /**
     * Find all seats for a specific venue.
     *
     * @param venue the venue to find seats for
     * @return a list of seats for the given venue
     */
    List<Seat> findByVenue(@Param("venue") Venue venue);

    /**
     * Find all seats for a specific venue with pagination.
     *
     * @param venue    the venue to find seats for
     * @param pageable pagination and sorting information
     * @return a page of seats for the given venue
     */
    Page<Seat> findByVenue(@Param("venue") Venue venue, Pageable pageable);

    /**
     * Find all seats for a specific venue ID.
     *
     * @param venueId the ID of the venue to find seats for
     * @return a list of seats for the given venue ID
     */
    List<Seat> findByVenueId(@Param("venueId") Long venueId);

    /**
     * Find all seats for a specific venue ID with pagination.
     *
     * @param venueId  the ID of the venue to find seats for
     * @param pageable pagination and sorting information
     * @return a page of seats for the given venue ID
     */
    Page<Seat> findByVenueId(@Param("venueId") Long venueId, Pageable pageable);

    /**
     * Find all seats with a specific status.
     *
     * @param status the status to filter by
     * @return a list of seats with the given status
     */
    List<Seat> findByStatus(@Param("status") SeatStatus status);

    /**
     * Find all seats with a specific status with pagination.
     *
     * @param status   the status to filter by
     * @param pageable pagination and sorting information
     * @return a page of seats with the given status
     */
    Page<Seat> findByStatus(@Param("status") SeatStatus status, Pageable pageable);

    /**
     * Find all seats for a specific venue and section.
     *
     * @param venue   the venue to find seats for
     * @param section the section to filter by
     * @return a list of seats for the given venue and section
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section")
    List<Seat> findByVenueAndSection(
            @Param("venue") Venue venue,
            @Param("section") String section
    );

    /**
     * Find all seats for a specific venue and section with pagination.
     *
     * @param venue    the venue to find seats for
     * @param section  the section to filter by
     * @param pageable pagination and sorting information
     * @return a page of seats for the given venue and section
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section")
    Page<Seat> findByVenueAndSection(
            @Param("venue") Venue venue,
            @Param("section") String section,
            Pageable pageable
    );

    /**
     * Find all seats for a specific venue and row number.
     *
     * @param venue     the venue to find seats for
     * @param rowNumber the row number to filter by
     * @return a list of seats for the given venue and row number
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.rowNumber = :rowNumber")
    List<Seat> findByVenueAndRow(
            @Param("venue") Venue venue,
            @Param("rowNumber") int rowNumber
    );

    /**
     * Find all seats for a specific venue and row number with pagination.
     *
     * @param venue     the venue to find seats for
     * @param rowNumber the row number to filter by
     * @param pageable  pagination and sorting information
     * @return a page of seats for the given venue and row number
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.rowNumber = :rowNumber")
    Page<Seat> findByVenueAndRow(
            @Param("venue") Venue venue,
            @Param("rowNumber") int rowNumber,
            Pageable pageable
    );

    /**
     * Find all seats for a specific venue, section, and row number.
     *
     * @param venue     the venue to find seats for
     * @param section   the section to filter by
     * @param rowNumber the row number to filter by
     * @return a list of seats matching the criteria
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section AND s.rowNumber = :rowNumber")
    List<Seat> findByVenueSectionAndRow(
            @Param("venue") Venue venue,
            @Param("section") String section,
            @Param("rowNumber") int rowNumber
    );

    /**
     * Find all seats for a specific venue, section, and row number with pagination.
     *
     * @param venue     the venue to find seats for
     * @param section   the section to filter by
     * @param rowNumber the row number to filter by
     * @param pageable  pagination and sorting information
     * @return a page of seats matching the criteria
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section AND s.rowNumber = :rowNumber")
    Page<Seat> findByVenueSectionAndRow(
            @Param("venue") Venue venue,
            @Param("section") String section,
            @Param("rowNumber") int rowNumber,
            Pageable pageable
    );

    /**
     * Find a specific seat by venue, section, and seat number.
     *
     * @param venue      the venue of the seat
     * @param section    the section of the seat
     * @param seatNumber the seat number
     * @return an Optional containing the seat if found, empty otherwise
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section AND s.seatNumber = :seatNumber")
    Optional<Seat> findByVenueSectionAndSeatNumber(
            @Param("venue") Venue venue,
            @Param("section") String section,
            @Param("seatNumber") int seatNumber
    );

    /**
     * Count all available seats for a specific venue.
     *
     * @param venue the venue to count available seats for
     * @return the number of available seats
     */
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.venue = :venue AND s.status.name = 'AVAILABLE'")
    int countAvailableSeatsByVenue(@Param("venue") Venue venue);

    /**
     * Count all seats for a specific venue by status.
     *
     * @param venue  the venue to count seats for
     * @param status the status to filter by
     * @return the number of seats with the given status
     */
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.venue = :venue AND s.status = :status")
    int countSeatsByVenueAndStatus(
            @Param("venue") Venue venue,
            @Param("status") SeatStatus status
    );

    /**
     * Find all available seats for a specific venue.
     *
     * @param venue the venue to find available seats for
     * @return a list of available seats
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.status.name = 'AVAILABLE'")
    List<Seat> findAvailableSeatsByVenue(@Param("venue") Venue venue);

    /**
     * Find all available seats for a specific venue with pagination.
     *
     * @param venue    the venue to find available seats for
     * @param pageable pagination and sorting information
     * @return a page of available seats
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.status.name = 'AVAILABLE'")
    Page<Seat> findAvailableSeatsByVenue(
            @Param("venue") Venue venue,
            Pageable pageable
    );

    /**
     * Find all available seats for a specific venue and section.
     *
     * @param venue   the venue to find available seats for
     * @param section the section to filter by
     * @return a list of available seats in the specified section
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.status.name = 'AVAILABLE' AND s.section = :section")
    List<Seat> findAvailableSeatsByVenueAndSection(
            @Param("venue") Venue venue,
            @Param("section") String section
    );

    /**
     * Find all available seats for a specific venue and section with pagination.
     *
     * @param venue    the venue to find available seats for
     * @param section  the section to filter by
     * @param pageable pagination and sorting information
     * @return a page of available seats in the specified section
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.status.name = 'AVAILABLE' AND s.section = :section")
    Page<Seat> findAvailableSeatsByVenueAndSection(
            @Param("venue") Venue venue,
            @Param("section") String section,
            Pageable pageable
    );

    /**
     * Find all distinct sections for a specific venue.
     *
     * @param venue the venue to find sections for
     * @return a list of distinct section names, ordered alphabetically
     */
    @Query("SELECT DISTINCT s.section FROM Seat s WHERE s.venue = :venue ORDER BY s.section")
    List<String> findSectionsByVenue(@Param("venue") Venue venue);

    /**
     * Find all distinct row numbers for a specific venue and section.
     *
     * @param venue   the venue to find row numbers for
     * @param section the section to filter by
     * @return a list of distinct row numbers, ordered numerically
     */
    @Query("SELECT DISTINCT s.rowNumber FROM Seat s WHERE s.venue = :venue AND s.section = :section ORDER BY s.rowNumber")
    List<Integer> findRowNumbersByVenueAndSection(
            @Param("venue") Venue venue,
            @Param("section") String section
    );

    /**
     * Find all distinct seat numbers for a specific venue, section, and row.
     *
     * @param venue     the venue to find seat numbers for
     * @param section   the section to filter by
     * @param rowNumber the row number to filter by
     * @return a list of distinct seat numbers, ordered numerically
     */
    @Query("SELECT DISTINCT s.seatNumber FROM Seat s WHERE s.venue = :venue AND s.section = :section AND s.rowNumber = :rowNumber ORDER BY s.seatNumber")
    List<Integer> findSeatNumbersByVenueSectionAndRow(
            @Param("venue") Venue venue,
            @Param("section") String section,
            @Param("rowNumber") int rowNumber
    );

    /**
     * Find a specific seat by venue, section, row number, and seat number.
     *
     * @param venue      the venue of the seat
     * @param section    the section of the seat
     * @param rowNumber  the row number of the seat
     * @param seatNumber the seat number
     * @return an Optional containing the seat if found, empty otherwise
     */
    @Query("SELECT s FROM Seat s WHERE s.venue = :venue AND s.section = :section AND s.rowNumber = :rowNumber AND s.seatNumber = :seatNumber")
    Optional<Seat> findByVenueAndSeatDetails(
            @Param("venue") Venue venue,
            @Param("section") String section,
            @Param("rowNumber") int rowNumber,
            @Param("seatNumber") int seatNumber
    );

    /**
     * Check if a seat with the given section, row, and number exists in the venue.
     *
     * @param venue      the venue to check in
     * @param section    the section to check
     * @param rowNumber  the row number to check
     * @param seatNumber the seat number to check
     * @return true if the seat exists, false otherwise
     */
    @Query("SELECT COUNT(s) > 0 FROM Seat s WHERE s.venue = :venue AND s.section = :section AND s.rowNumber = :rowNumber AND s.seatNumber = :seatNumber")
    boolean existsByVenueAndSeatDetails(
            @Param("venue") Venue venue,
            @Param("section") String section,
            @Param("rowNumber") int rowNumber,
            @Param("seatNumber") int seatNumber
    );

    /**
     * Update the status of multiple seats in a single transaction.
     *
     * @param seatIds   the IDs of the seats to update
     * @param newStatus the new status to set
     * @return the number of seats updated
     */
    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.status = :newStatus WHERE s.id IN :seatIds")
    int updateSeatsStatus(
            @Param("seatIds") List<Long> seatIds,
            @Param("newStatus") SeatStatus newStatus
    );

    /**
     * Find all seats by their IDs.
     *
     * @param seatIds the IDs of the seats to find
     * @return a list of seats with the given IDs
     */
    @Query("SELECT s FROM Seat s WHERE s.id IN :seatIds")
    List<Seat> findByIds(@Param("seatIds") List<Long> seatIds);
}
