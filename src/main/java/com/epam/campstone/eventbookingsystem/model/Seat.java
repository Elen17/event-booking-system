package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "seat")
@Getter
@Setter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "section")
    private String section;

    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private Double basePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private SeatStatus status;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "last_booking_update")
    private LocalDateTime lastBookingUpdate = LocalDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventSeat> eventSeats = new HashSet<>();

    // Helper methods for bidirectional relationships
    public void addEventSeat(EventSeat eventSeat) {
        eventSeats.add(eventSeat);
        eventSeat.setSeat(this);
    }

    public void removeEventSeat(EventSeat eventSeat) {
        eventSeats.remove(eventSeat);
        eventSeat.setSeat(null);
    }

    // Business logic methods
    public boolean isBookedForEvent(Event event) {
        return eventSeats.stream()
                .anyMatch(es -> es.getEvent().equals(event) &&
                        (es.getStatus() == SeatStatus.RESERVED || es.getStatus() == SeatStatus.PURCHASED));
    }

    public void updateStatus(SeatStatus newStatus) {
        this.status = newStatus;
        this.isAvailable = newStatus == SeatStatus.AVAILABLE;
        this.lastBookingUpdate = LocalDateTime.now();
    }
}
