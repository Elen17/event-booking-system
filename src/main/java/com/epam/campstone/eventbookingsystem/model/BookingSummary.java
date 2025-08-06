package com.epam.campstone.eventbookingsystem.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_summary")
@Data
@NoArgsConstructor
public class BookingSummary {
    @Id
    @Column(name = "group_reference")
    private String groupReference;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "total_tickets")
    private Integer totalTickets;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "individual_bookings")
    private Long individualBookings;

    @Column(name = "event_title")
    private String eventTitle;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;
}
