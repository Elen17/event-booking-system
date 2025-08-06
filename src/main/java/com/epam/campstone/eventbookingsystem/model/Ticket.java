package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"booking_id", "seat_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", unique = true, nullable = false, length = 20)
    private String ticketNumber;

    @Column(name = "ticket_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus = TicketStatus.ACTIVE;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "qr_code")
    private String qrCode;

    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Helper method to mark ticket as used
    public void markAsUsed() {
        this.ticketStatus = TicketStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    // Helper method to cancel ticket
    public void cancel() {
        this.ticketStatus = TicketStatus.CANCELLED;
    }

    // Helper method to check if ticket is valid for use
    public boolean isValidForUse() {
        return this.ticketStatus == TicketStatus.ACTIVE && this.usedAt == null;
    }
    public enum TicketStatus {
        ACTIVE("Active"),
        USED("Used"),
        CANCELLED("Cancelled");

        private final String displayName;

        TicketStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
