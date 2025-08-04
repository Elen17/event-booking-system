package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "event_status")
@Getter
public enum EventStatus {
    PLANNED("Planned", "Event is scheduled but not yet active"),
    CANCELLED("Cancelled", "Event has been cancelled"),
    COMPLETED("Completed", "Event has taken place");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    EventStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
