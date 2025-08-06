package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.BookingSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingSummaryRepository extends JpaRepository<BookingSummary, String> {

    List<BookingSummary> findByUserId(Long userId);

    List<BookingSummary> findByEventId(Long eventId);

    @Query("SELECT bs FROM BookingSummary bs WHERE bs.eventDate BETWEEN :startDate AND :endDate")
    List<BookingSummary> findByEventDateBetween(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT bs FROM BookingSummary bs WHERE bs.createdAt BETWEEN :startDate AND :endDate")
    List<BookingSummary> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT bs FROM BookingSummary bs WHERE bs.userId = :userId ORDER BY bs.createdAt DESC")
    List<BookingSummary> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT SUM(bs.totalAmount) FROM BookingSummary bs WHERE bs.eventId = :eventId")
    Double getTotalRevenueByEventId(@Param("eventId") Long eventId);

    @Query("SELECT SUM(bs.totalTickets) FROM BookingSummary bs WHERE bs.eventId = :eventId")
    Long getTotalTicketsSoldByEventId(@Param("eventId") Long eventId);
}
