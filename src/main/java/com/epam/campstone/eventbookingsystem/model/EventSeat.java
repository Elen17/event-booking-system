package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "event_seat")
public class EventSeat {
    @EmbeddedId
    private EventSeatId id;

    @MapsId("eventId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @MapsId("seatId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private SeatStatus status;

    // Helper methods for bidirectional relationships
    public void setEvent(Event event) {
        this.event = event;
        this.id.setEventId(event != null ? event.getId() : null);
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
        this.id.setSeatId(seat != null ? seat.getId() : null);
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
        /*if (booking != null) {
            this.status = SeatStatus.RESERVED;
        } else if (this.status == SeatStatus.RESERVED) {
            this.status = SeatStatus.AVAILABLE;
        }*/
    }
}
