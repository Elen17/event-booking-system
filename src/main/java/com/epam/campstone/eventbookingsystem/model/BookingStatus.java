package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "booking_status")
@Getter
public enum BookingStatus {
    TEMPORARY_HOLD("Temporary Hold", "Seat temporarily reserved (2 days max)", true),
    CONFIRMED_BOOKING("Confirmed Booking", "Booking confirmed but not yet paid", true),
    PURCHASED("Purchased", "Seat purchased and paid for", true),
    EXPIRED("Expired", "Temporary booking expired", false),
    CANCELLED("Cancelled", "Booking cancelled by user", false);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "is_active")
    private boolean isActive;

    BookingStatus(String name, String description, boolean isActive) {
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }
}
