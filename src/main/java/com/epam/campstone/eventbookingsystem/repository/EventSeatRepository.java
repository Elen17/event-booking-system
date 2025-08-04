package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.EventSeat;
import com.epam.campstone.eventbookingsystem.model.Seat;
import com.epam.campstone.eventbookingsystem.model.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {

    List<EventSeat> findByEvent(Event event);
    List<EventSeat> findBySeat(Seat seat);
    List<EventSeat> findByStatus(SeatStatus status);

    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.status = 'AVAILABLE'")
    List<EventSeat> findAvailableByEvent(@Param("event") Event event);

    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.seat = :seat")
    Optional<EventSeat> findByEventAndSeat(@Param("event") Event event, @Param("seat") Seat seat);

    @Query("SELECT COUNT(es) FROM EventSeat es WHERE es.event = :event AND es.status = 'AVAILABLE'")
    int countAvailableSeatsForEvent(@Param("event") Event event);

    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.status = 'AVAILABLE' ORDER BY es.seat.section, es.seat.rowNumber, es.seat.seatNumber")
    List<EventSeat> findAvailableSeatsForEventOrdered(@Param("event") Event event);

    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.seat.section = :section AND es.status = 'AVAILABLE'")
    List<EventSeat> findAvailableByEventAndSection(
        @Param("event") Event event,
        @Param("section") String section
    );

    @Modifying
    @Query("UPDATE EventSeat es SET es.status = 'RESERVED', es.booking = :booking WHERE es.event = :event AND es.seat = :seat AND es.status = 'AVAILABLE'")
    int reserveSeat(
        @Param("event") Event event,
        @Param("seat") Seat seat,
        @Param("booking") Booking booking
    );

    @Modifying
    @Query("UPDATE EventSeat es SET es.status = 'PURCHASED' WHERE es.booking = :booking")
    int markSeatsAsPurchased(@Param("booking") Booking booking);

    @Modifying
    @Query("UPDATE EventSeat es SET es.status = 'AVAILABLE', es.booking = NULL WHERE es.booking = :booking")
    int releaseSeatsForBooking(@Param("booking") Booking booking);

    @Query("SELECT es FROM EventSeat es WHERE es.booking = :booking")
    List<EventSeat> findByBooking(@Param("booking") Booking booking);

    @Query("SELECT COUNT(es) > 0 FROM EventSeat es WHERE es.event = :event AND es.seat = :seat AND es.status = 'AVAILABLE'")
    boolean isSeatAvailableForEvent(@Param("event") Event event, @Param("seat") Seat seat);

    @Query("SELECT es FROM EventSeat es WHERE es.event = :event AND es.seat.section = :section AND es.seat.rowNumber = :rowNumber")
    List<EventSeat> findByEventAndSectionAndRow(
        @Param("event") Event event,
        @Param("section") String section,
        @Param("rowNumber") int rowNumber
    );
}
