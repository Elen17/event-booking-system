package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class EventSeatId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "seat_id")
    private Long seatId;

    // Default constructor required by JPA
    public EventSeatId() {}

    // Convenience constructor
    public EventSeatId(Long eventId, Long seatId) {
        this.eventId = eventId;
        this.seatId = seatId;
    }
}
