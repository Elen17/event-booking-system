package com.epam.campstone.eventbookingsystem.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "ticket_details")
@Data
@NoArgsConstructor
public class TicketDetails {
    @Id
    @Column(name = "ticket_number")
    private String ticketNumber;

    @Column(name = "ticket_status")
    private String ticketStatus;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "booking_reference")
    private String bookingReference;

    private String section;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "event_title")
    private String eventTitle;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "venue_address")
    private String venueAddress;
}
