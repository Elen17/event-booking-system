package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.Booking;
import com.epam.campstone.eventbookingsystem.model.BookingStatus;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    Page<Booking> findByUser(User user, Pageable pageable);
    List<Booking> findByEvent(Event event);
    Page<Booking> findByEvent(Event event, Pageable pageable);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByStatusIn(List<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.status = 'PURCHASED'")
    List<Booking> findPurchasedByUser(@Param("user") User user);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.event = :event")
    List<Booking> findByUserAndEvent(@Param("user") User user, @Param("event") Event event);

    @Query("SELECT b FROM Booking b WHERE b.bookingReference = :reference")
    Optional<Booking> findByBookingReference(@Param("reference") String reference);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.status = 'TEMPORARY_HOLD' AND b.expiresAt < :now")
    List<Booking> findExpiredTemporaryHolds(@Param("user") User user, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.status = 'TEMPORARY_HOLD' AND b.expiresAt < :now")
    List<Booking> findAllExpiredTemporaryHolds(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.user = :user AND b.event = :event AND b.status = 'PURCHASED'")
    boolean hasUserPurchasedEvent(@Param("user") User user, @Param("event") Event event);

    @Modifying
    @Query("UPDATE Booking b SET b.status = 'EXPIRED' WHERE b.status = 'TEMPORARY_HOLD' AND b.expiresAt < :now")
    int expireTemporaryHolds(@Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.event = :event AND b.status = 'PURCHASED'")
    List<Booking> findPurchasedBookingsByUserAndEvent(@Param("user") User user, @Param("event") Event event);

    @Query("SELECT b FROM Booking b WHERE b.event = :event AND b.status = 'PURCHASED'")
    List<Booking> findPurchasedBookingsForEvent(@Param("event") Event event);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event = :event AND b.status = 'PURCHASED'")
    int countPurchasedTicketsForEvent(@Param("event") Event event);

    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.id = :id AND b.user.email = :userEmail")
    Optional<Booking> findByIdAndUserEmail(Long id, String userEmail);

    @Query("SELECT b FROM Booking b WHERE b.user.email = :userEmail ORDER BY b.createdAt DESC")
    List<Booking> findByUserEmailOrderByBookingDateDesc(String userEmail);
}
