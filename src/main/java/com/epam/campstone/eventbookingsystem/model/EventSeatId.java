package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class EventSeatId implements Serializable {
    private static final long serialVersionUID = -1720736105812457493L;
    @NotNull
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @NotNull
    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventSeatId entity = (EventSeatId) o;
        return Objects.equals(this.eventId, entity.eventId) &&
                Objects.equals(this.seatId, entity.seatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, seatId);
    }

}