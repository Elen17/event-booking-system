package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "event_type")
@Getter
public enum EventType {
    CONCERT("Concert", "Live music performance"),
    SPORT("Sport", "Sporting event"),
    FESTIVAL("Festival", "Multi-day cultural event"),
    CINEMA("Cinema", "Movie screening"),
    THEATER("Theater", "Theatrical performance"),
    OPERA_BALLET("Opera and Ballet", "Classical music and dance performance"),
    CONFERENCE("Conference", "Business or academic conference"),
    EXHIBITION("Exhibition", "Art or trade show");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    EventType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
