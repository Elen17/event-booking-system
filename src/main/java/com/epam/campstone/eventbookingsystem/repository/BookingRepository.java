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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Find all bookings for a specific user with pagination and sorting
     *
     * @param user     the user to find bookings for
     * @param pageable pagination and sorting information
     * @return page of bookings
     */
    Page<Booking> findByUser(User user, Pageable pageable);

    /**
     * Find all bookings for a specific user (without pagination)
     *
     * @param user the user to find bookings for
     * @return list of bookings
     */
    List<Booking> findByUser(User user);

    List<Booking> findByEvent(Event event);

    Page<Booking> findByEvent(Event event, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.bookingStatus.name = 'PURCHASED'")
    List<Booking> findPurchasedByUser(@Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.event = :event")
    List<Booking> findByUserAndEvent(@Param("user") User user, @Param("event") Event event);

    @Query("SELECT b FROM Booking b WHERE b.bookingReference = :reference")
    Optional<Booking> findByBookingReference(@Param("reference") String reference);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.bookingStatus.name = 'TEMPORARY_HOLD' AND b.expiresAt < :now")
    List<Booking> findExpiredTemporaryHolds(@Param("user") User user, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.bookingStatus.name = 'TEMPORARY_HOLD' AND b.expiresAt < :now")
    List<Booking> findAllExpiredTemporaryHolds(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.user = :user AND b.event = :event AND b.bookingStatus.name = 'PURCHASED'")
    boolean hasUserPurchasedEvent(@Param("user") User user, @Param("event") Event event);

    /**
     * Expire all temporary hold bookings that have passed their expiration time
     *
     * @param now current timestamp
     * @return number of expired bookings
     */
    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.bookingStatus.name = 'EXPIRED' " +
            "WHERE b.bookingStatus.name = 'TEMPORARY_HOLD' AND b.expiresAt < :now")
    int expireTemporaryHolds(@Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.event = :event AND b.bookingStatus.name = 'PURCHASED'")
    List<Booking> findPurchasedBookingsByUserAndEvent(@Param("user") User user, @Param("event") Event event);

    @Query("SELECT b FROM Booking b WHERE b.event = :event AND b.bookingStatus.name = 'PURCHASED'")
    List<Booking> findPurchasedBookingsForEvent(@Param("event") Event event);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event = :event AND b.bookingStatus.name = 'PURCHASED'")
    int countPurchasedTicketsForEvent(@Param("event") Event event);

    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    /**
     * Find all bookings created between two dates
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return list of bookings created between the given dates
     */
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Find all bookings created between two dates with pagination
     *
     * @param startDate start date (inclusive)
     * @param endDate   end date (inclusive)
     * @param pageable  pagination and sorting information
     * @return page of bookings created between the given dates
     */
    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    Page<Booking> findBookingsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * Find all bookings for a user created between two dates
     *
     * @param user      the user to filter by
     * @param startDate start date (inclusive)
     * @param endDate   end date (inclusive)
     * @param pageable  pagination and sorting information
     * @return page of bookings for the user created between the given dates
     */
    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.createdAt BETWEEN :startDate AND :endDate")
    Page<Booking> findByUserAndCreatedAtBetween(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b WHERE b.id = :id AND b.user.email = :userEmail")
    Optional<Booking> findByIdAndUserEmail(Long id, String userEmail);

    /**
     * Find all bookings for a user by email, ordered by creation date (newest first)
     *
     * @param userEmail the user's email
     * @return list of bookings for the user
     */
    @Query("SELECT b FROM Booking b WHERE b.user.email = :userEmail ORDER BY b.createdAt DESC")
    List<Booking> findByUserEmailOrderByBookingDateDesc(@Param("userEmail") String userEmail);

    /**
     * Find all bookings for a user by email with pagination, ordered by creation date (newest first)
     *
     * @param userEmail the user's email
     * @param pageable  pagination and sorting information
     * @return page of bookings for the user
     */
    @Query("SELECT b FROM Booking b WHERE b.user.email = :userEmail")
    Page<Booking> findByUserEmail(
            @Param("userEmail") String userEmail,
            Pageable pageable
    );

    /**
     * Check if a booking with the given ID exists and belongs to the specified user
     *
     * @param id     booking ID
     * @param userId user ID
     * @return true if the booking exists and belongs to the user, false otherwise
     */
    boolean existsByIdAndUserId(Long id, Long userId);

    @Query("SELECT es.seat FROM EventSeat es WHERE es.booking.id = :booking")
    List<Seat> findSeatsByBooking(Long bookingId);
}
