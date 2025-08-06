package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.dto.BookingDto;
import com.epam.campstone.eventbookingsystem.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    /**
     * Create a new booking
     *
     * @param userEmail the email of the user making the booking
     * @param bookingDto the booking details
     * @return the created booking
     */
    Booking createBooking(String userEmail, BookingDto bookingDto);

    /**
     * Find a booking by ID and user email
     *
     * @param id the booking ID
     * @param userEmail the user's email
     * @return an Optional containing the booking if found
     */
    Optional<Booking> findByIdAndUserEmail(Long id, String userEmail);

    /**
     * Cancel a booking
     *
     * @param bookingId the booking ID
     * @param userEmail the user's email (for authorization)
     * @return true if the booking was cancelled, false otherwise
     */
    boolean cancelBooking(Long bookingId, String userEmail);

    /**
     * Find all bookings for a user
     *
     * @param userEmail the user's email
     * @return list of the user's bookings
     */
    List<Booking> findUserBookings(String userEmail);

    /**
     * Find tickets for a specific booking
     *
     * @param bookingId the booking ID
     * @return list of tickets for the booking
     */
    List<Ticket> findTicketsByBookingId(Long bookingId);
}
