package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", unique = true, nullable = false, length = 20)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_status_id", nullable = false)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // Business logic methods
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isConfirmed() {
        return confirmedAt != null;
    }

    public boolean isPurchased() {
        return purchasedAt != null;
    }

    public boolean isCancelled() {
        return cancelledAt != null;
    }

    public boolean canBeConfirmed() {
        return !isCancelled() && !isExpired() && !isPurchased();
    }

    // Helper methods for status transitions
    public void confirm() {
        if (canBeConfirmed()) {
            this.status = BookingStatus.CONFIRMED_BOOKING;
            this.confirmedAt = LocalDateTime.now();
        }
    }

    public void purchase() {
        if (status == BookingStatus.CONFIRMED_BOOKING) {
            this.status = BookingStatus.PURCHASED;
            this.purchasedAt = LocalDateTime.now();
            this.paymentDate = LocalDateTime.now();
        }
    }

    public void cancel() {
        if (!isCancelled() && !isPurchased()) {
            this.status = BookingStatus.CANCELLED;
            this.cancelledAt = LocalDateTime.now();
        }
    }
}
