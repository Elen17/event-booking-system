package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "seat_status")
@Getter
public enum SeatStatus {
    AVAILABLE("Available", "Seat is available for booking"),
    RESERVED("Reserved", "Seat is currently reserved"),
    PURCHASED("Purchased", "Seat has been purchased");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    SeatStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
