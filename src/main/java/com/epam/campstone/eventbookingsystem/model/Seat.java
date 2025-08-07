package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "seat")
public class Seat {
    @Id
    @ColumnDefault("nextval('seat_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Size(max = 50)
    @Column(name = "section", length = 50)
    private String section;

    @NotNull
    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;

    @NotNull
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @NotNull
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private SeatStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_booking_id")
    private Booking currentBooking;

    @ColumnDefault("true")
    @Column(name = "is_available")
    private Boolean isAvailable;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "last_booking_update")
    private Instant lastBookingUpdate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

}