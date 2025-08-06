package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "event")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private EventStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private EventType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingGroup> bookingGroups;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventSeat> eventSeats = new HashSet<>();
    @Column(name = "available_attendees_capacity")
    private Integer availableAttendeesCapacity;

    // Business logic methods
    public boolean isUpcoming() {
        LocalDate today = LocalDate.now();
        return eventDate.isAfter(today) || 
              (eventDate.isEqual(today) && startTime.isAfter(LocalTime.now()));
    }

    public boolean isCompleted() {
        LocalDate today = LocalDate.now();
        return eventDate.isBefore(today) || 
              (eventDate.isEqual(today) && endTime.isBefore(LocalTime.now()));
    }

    // Helper methods for bidirectional relationships
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setEvent(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setEvent(null);
    }

    public void addEventSeat(EventSeat eventSeat) {
        eventSeats.add(eventSeat);
        eventSeat.setEvent(this);
    }

    public void removeEventSeat(EventSeat eventSeat) {
        eventSeats.remove(eventSeat);
        eventSeat.setEvent(null);
    }

    public @NotNull(message = "Capacity is required") @Min(value = 1, message = "Capacity must be at least 1") Integer getAvailableAttendeesCapacity() {
        return availableAttendeesCapacity;
    }

    public void setAvailableAttendeesCapacity(Integer availableAttendeesCapacity) {
        this.availableAttendeesCapacity = availableAttendeesCapacity;
    }
}
