package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "booking_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class BookingGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_reference", unique = true, nullable = false, length = 20)
    private String groupReference;

    @Column(name = "total_tickets", nullable = false)
    private Integer totalTickets;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "bookingGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    // Helper methods
    public void addBooking(Booking booking) {
        if (bookings != null) {
            bookings.add(booking);
            booking.setBookingGroup(this);
        }
    }

    public void removeBooking(Booking booking) {
        if (bookings != null) {
            bookings.remove(booking);
            booking.setBookingGroup(null);
        }
    }

    public int getActualBookingCount() {
        return bookings != null ? bookings.size() : 0;
    }
}
