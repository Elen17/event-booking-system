package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.*;
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
 * Repository interface for {@link EventSeat} entities.
 * Provides methods to interact with event seat data in the database.
 */
@Repository
@Transactional(readOnly = true)
public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {

    /**
     * Find all event seats for a specific event with pagination.
     *
     * @param event    the event to find seats for
     * @param pageable pagination and sorting information
     * @return a page of event seats for the given event
     */
    Page<EventSeat> findByEvent(@Param("event") Event event, Pageable pageable);

    /**
     * Find all event seats for a specific event.
     *
     * @param event the event to find seats for
     * @return a list of event seats for the given event
     */
    List<EventSeat> findByEvent(@Param("event") Event event);

    /**
     * Find all event seats for a specific seat.
     *
     * @param seat the seat to find events for
     * @return a list of event seats for the given seat
     */
    List<EventSeat> findBySeat(@Param("seat") Seat seat);

    /**
     * Find all event seats with a specific status.
     *
     * @param status the status to filter by
     * @return a list of event seats with the given status
     */
    List<EventSeat> findByStatus(@Param("status") SeatStatus status);

    /**
     * Find all available seats for a specific event.
     *
     * @param event the event to find available seats for
     * @return a list of available event seats
     */
    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.status.name = 'AVAILABLE'")
    List<EventSeat> findAvailableByEvent(@Param("event") Event event);

    /**
     * Find all available seats for a specific event with pagination.
     *
     * @param event    the event to find available seats for
     * @param pageable pagination and sorting information
     * @return a page of available event seats
     */
    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.status.name = 'AVAILABLE'")
    Page<EventSeat> findAvailableByEvent(@Param("event") Event event, Pageable pageable);

    /**
     * Find a specific event seat by event and seat.
     *
     * @param event the event
     * @param seat  the seat
     * @return an Optional containing the event seat if found, empty otherwise
     */
    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.seat = :seat")
    Optional<EventSeat> findByEventAndSeat(
            @Param("event") Event event,
            @Param("seat") Seat seat
    );

    /**
     * Count available seats for a specific event.
     *
     * @param event the event to count available seats for
     * @return the number of available seats
     */
    @Query("SELECT COUNT(es) FROM EventSeat es WHERE es.event = :event AND es.status.name = 'AVAILABLE'")
    int countAvailableSeatsForEvent(@Param("event") Event event);

    /**
     * Count seats for a specific event by status.
     *
     * @param event  the event to count seats for
     * @param status the status to filter by
     * @return the number of seats with the given status
     */
    @Query("SELECT COUNT(es) FROM EventSeat es WHERE es.event = :event AND es.status = :status")
    int countSeatsByEventAndStatus(
            @Param("event") Event event,
            @Param("status") SeatStatus status
    );

    /**
     * Find all available seats for a specific event, ordered by section, row, and seat number.
     *
     * @param event the event to find available seats for
     * @return a list of available event seats, ordered by section, row, and seat number
     */
    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.status.name = 'AVAILABLE' ORDER BY es.seat.section, es.seat.rowNumber, es.seat.seatNumber")
    List<EventSeat> findAvailableSeatsForEventOrdered(@Param("event") Event event);

    /**
     * Find all available seats for a specific event, ordered by section, row, and seat number, with pagination.
     *
     * @param event    the event to find available seats for
     * @param pageable pagination and sorting information
     * @return a page of available event seats, ordered by section, row, and seat number
     */
    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.status.name = 'AVAILABLE'")
    Page<EventSeat> findAvailableSeatsForEventOrdered(
            @Param("event") Event event,
            Pageable pageable
    );

    /**
     * Find all available seats for a specific event and section.
     *
     * @param event   the event to find available seats for
     * @param section the section to filter by
     * @return a list of available event seats in the specified section
     */
    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.seat.section = :section AND es.status.name = 'AVAILABLE'")
    List<EventSeat> findAvailableByEventAndSection(
            @Param("event") Event event,
            @Param("section") String section
    );

    /**
     * Find all available seats for a specific event and section with pagination.
     *
     * @param event    the event to find available seats for
     * @param section  the section to filter by
     * @param pageable pagination and sorting information
     * @return a page of available event seats in the specified section
     */
    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.seat.section = :section AND es.status.name = 'AVAILABLE'")
    Page<EventSeat> findAvailableByEventAndSection(
            @Param("event") Event event,
            @Param("section") String section,
            Pageable pageable
    );

    /**
     * Find all seats for a specific booking.
     *
     * @param booking the booking to find seats for
     * @return a list of event seats for the given booking
     */
    @Query("SELECT es FROM EventSeat es WHERE es.booking = :booking")
    List<EventSeat> findByBooking(@Param("booking") Booking booking);

    /**
     * Reserve a specific seat for an event.
     *
     * @param event   the event
     * @param seat    the seat to reserve
     * @param booking the booking to associate with the seat
     * @return the number of seats reserved (1 if successful, 0 otherwise)
     */
    @Modifying
    @Transactional
    @Query("UPDATE EventSeat es SET es.status.name = 'RESERVED', es.booking = :booking WHERE es.event = :event AND es.seat = :seat AND es.status.name = 'AVAILABLE'")
    int reserveSeat(
            @Param("event") Event event,
            @Param("seat") Seat seat,
            @Param("booking") Booking booking
    );

    /**
     * Reserve multiple seats for an event in a single transaction.
     *
     * @param event   the event
     * @param seats   the list of seats to reserve
     * @param booking the booking to associate with the seats
     * @return the number of seats successfully reserved
     */
    @Modifying
    @Transactional
    @Query("UPDATE EventSeat es SET es.status.name = 'RESERVED', es.booking = :booking WHERE es.event = :event AND es.seat IN :seats AND es.status.name = 'AVAILABLE'")
    int reserveSeats(
            @Param("event") Event event,
            @Param("seats") List<Seat> seats,
            @Param("booking") Booking booking
    );

    /**
     * Mark all seats in a booking as purchased.
     *
     * @param booking the booking to mark as purchased
     * @return the number of seats updated
     */
    @Modifying
    @Transactional
    @Query("UPDATE EventSeat es SET es.status.name = 'PURCHASED' WHERE es.booking = :booking")
    int markSeatsAsPurchased(@Param("booking") Booking booking);

    /**
     * Release all reserved seats for a booking (set status back to AVAILABLE and remove booking reference).
     *
     * @param booking the booking to release seats for
     * @return the number of seats released
     */
    @Modifying
    @Transactional
    @Query("UPDATE EventSeat es SET es.status.name = 'AVAILABLE', es.booking = null WHERE es.booking = :booking AND es.status.name = 'RESERVED'")
    int releaseReservedSeats(@Param("booking") Booking booking);

    /**
     * Find all event seats for a specific event and status.
     *
     * @param event  the event to find seats for
     * @param status the status to filter by
     * @return a list of event seats with the given status
     */
    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.status = :status")
    List<EventSeat> findByEventAndStatus(
            @Param("event") Event event,
            @Param("status") SeatStatus status
    );

    /**
     * Check if a specific seat is available for an event.
     *
     * @param event the event to check
     * @param seat  the seat to check
     * @return true if the seat is available, false otherwise
     */
    @Query("SELECT COUNT(es) > 0 FROM EventSeat es WHERE es.event = :event AND es.seat = :seat AND es.status.name = 'AVAILABLE'")
    boolean isSeatAvailable(
            @Param("event") Event event,
            @Param("seat") Seat seat
    );

    @Modifying
    @Query("UPDATE EventSeat es SET es.status.name = 'AVAILABLE', es.booking = NULL WHERE es.booking = :booking")
    int releaseSeatsForBooking(@Param("booking") Booking booking);

    @Query("SELECT COUNT(es) > 0 FROM EventSeat es WHERE es.event = :event AND es.seat = :seat AND es.status.name = 'AVAILABLE'")
    boolean isSeatAvailableForEvent(@Param("event") Event event, @Param("seat") Seat seat);

    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.seat.section = :section AND es.seat.rowNumber = :rowNumber")
    List<EventSeat> findByEventAndSectionAndRow(
            @Param("event") Event event,
            @Param("section") String section,
            @Param("rowNumber") int rowNumber
    );
}
