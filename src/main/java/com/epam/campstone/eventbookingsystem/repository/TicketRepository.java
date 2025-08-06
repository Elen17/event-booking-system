package com.epam.campstone.eventbookingsystem.repository;
import com.epam.campstone.eventbookingsystem.model.Ticket;
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
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByBookingId(Long bookingId);

    List<Ticket> findByEventId(Long eventId);

    List<Ticket> findBySeatId(Long seatId);

    List<Ticket> findByTicketStatus(Ticket.TicketStatus ticketStatus);

    List<Ticket> findByEventIdAndTicketStatus(Long eventId, Ticket.TicketStatus ticketStatus);

    @Query("SELECT t FROM Ticket t JOIN FETCH t.booking b JOIN FETCH b.user WHERE b.user.id = :userId")
    List<Ticket> findByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Ticket t WHERE t.booking.user.id = :userId AND t.event.id = :eventId")
    List<Ticket> findByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT t FROM Ticket t JOIN FETCH t.seat s JOIN FETCH t.event e JOIN FETCH t.booking b WHERE t.id = :ticketId")
    Optional<Ticket> findByIdWithDetails(@Param("ticketId") Long ticketId);

    @Query("SELECT t FROM Ticket t WHERE t.ticketNumber = :ticketNumber AND t.ticketStatus = 'ACTIVE'")
    Optional<Ticket> findActiveTicketByNumber(@Param("ticketNumber") String ticketNumber);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event.id = :eventId AND t.ticketStatus = 'ACTIVE'")
    Long countActiveTicketsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event.id = :eventId AND t.ticketStatus = 'USED'")
    Long countUsedTicketsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT t FROM Ticket t WHERE t.issuedAt BETWEEN :startDate AND :endDate")
    List<Ticket> findTicketsIssuedBetween(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId AND t.usedAt IS NOT NULL ORDER BY t.usedAt DESC")
    List<Ticket> findUsedTicketsByEvent(@Param("eventId") Long eventId);

    @Modifying
    @Transactional
    @Query("UPDATE Ticket t SET t.ticketStatus = 'USED', t.usedAt = :usedAt WHERE t.ticketNumber = :ticketNumber")
    int markTicketAsUsed(@Param("ticketNumber") String ticketNumber, @Param("usedAt") LocalDateTime usedAt);

    @Modifying
    @Transactional
    @Query("UPDATE Ticket t SET t.ticketStatus = 'CANCELLED' WHERE t.booking.id = :bookingId")
    int cancelTicketsByBookingId(@Param("bookingId") Long bookingId);

    // Find tickets that need QR code generation
    @Query("SELECT t FROM Ticket t WHERE t.qrCode IS NULL AND t.ticketStatus = 'ACTIVE'")
    List<Ticket> findTicketsWithoutQrCode();

    // Find duplicate tickets (safety check)
    @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId AND t.seat.id = :seatId")
    List<Ticket> findDuplicateTickets(@Param("eventId") Long eventId, @Param("seatId") Long seatId);
}
