package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "venue")
@Getter
@Setter
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> events = new HashSet<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seat> seats = new HashSet<>();

    public static Venue create(String name, String address, City city) {
        Venue venue = new Venue();
        venue.setName(name);
        venue.setAddress(address);
        venue.setCity(city);
        return venue;
    }

    // Helper methods for bidirectional relationships
    public void addEvent(Event event) {
        events.add(event);
        event.setVenue(this);
    }

    public void removeEvent(Event event) {
        events.remove(event);
        event.setVenue(null);
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setVenue(this);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
        seat.setVenue(null);
    }
}
