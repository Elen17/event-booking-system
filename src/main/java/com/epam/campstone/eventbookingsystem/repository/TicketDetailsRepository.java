package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.TicketDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketDetailsRepository extends JpaRepository<TicketDetails, String> {

    @Query("SELECT td FROM TicketDetails td WHERE td.bookingReference = :bookingReference")
    List<TicketDetails> findByBookingReference(@Param("bookingReference") String bookingReference);

    @Query("SELECT td FROM TicketDetails td WHERE td.eventDate = :eventDate")
    List<TicketDetails> findByEventDate(@Param("eventDate") LocalDate eventDate);

    @Query("SELECT td FROM TicketDetails td WHERE td.venueName = :venueName AND td.eventDate = :eventDate")
    List<TicketDetails> findByVenueAndEventDate(@Param("venueName") String venueName,
                                                @Param("eventDate") LocalDate eventDate);

    @Query("SELECT td FROM TicketDetails td WHERE td.ticketStatus = :status")
    List<TicketDetails> findByTicketStatus(@Param("status") String status);

    @Query("SELECT td FROM TicketDetails td WHERE td.eventTitle LIKE %:eventTitle%")
    List<TicketDetails> findByEventTitleContaining(@Param("eventTitle") String eventTitle);
}
