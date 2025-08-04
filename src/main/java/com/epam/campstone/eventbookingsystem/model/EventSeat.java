package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "event_seat")
@Getter
@Setter
public class EventSeat {
    @EmbeddedId
    private EventSeatId id = new EventSeatId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seatId")
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private SeatStatus status;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

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
        if (booking != null) {
            this.status = SeatStatus.RESERVED;
        } else if (this.status == SeatStatus.RESERVED) {
            this.status = SeatStatus.AVAILABLE;
        }
    }

    // Business logic methods
    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public boolean isReserved() {
        return status == SeatStatus.RESERVED;
    }

    public boolean isPurchased() {
        return status == SeatStatus.PURCHASED;
    }
}
