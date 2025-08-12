package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.dto.BookingDto;
import com.epam.campstone.eventbookingsystem.dto.BookingStatus;
import com.epam.campstone.eventbookingsystem.dto.SeatDto;
import com.epam.campstone.eventbookingsystem.exception.ResourceNotFoundException;
import com.epam.campstone.eventbookingsystem.model.Booking;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.Seat;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.repository.*;
import com.epam.campstone.eventbookingsystem.service.api.BookingService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final SeatStatusRepository seatStatusRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              EventRepository eventRepository,
                              UserRepository userRepository,
                              SeatRepository seatRepository,
                              BookingStatusRepository bookingStatusRepository,
                              SeatStatusRepository seatStatusRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.bookingStatusRepository = bookingStatusRepository;
        this.seatStatusRepository = seatStatusRepository;
    }

    @Override
    public Booking createBooking(String userEmail, BookingDto bookingDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Event event = eventRepository.findById(bookingDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + bookingDto.getEventId()));

        // Check if there are enough available spots
        if (event.getAvailableAttendeesCapacity() < bookingDto.getSeats().size()) {
            throw new IllegalStateException("Not enough available spots for this event");
        }

        // Create booking
        Booking booking = new Booking();

        booking.setUser(user);
        booking.setEvent(event);
        booking.setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        booking.setPrice(this.calculateTotalPrice(bookingDto.getSeats()));
        booking.setBookingStatus(bookingStatusRepository.findByName("TEMPORARY_HOLD").orElse(null));
        booking.setSeats(this.createSeats(bookingDto.getSeats()));
//
        Booking savedBooking = bookingRepository.save(booking);

        // Update available spots
        event.setAvailableAttendeesCapacity(event.getAvailableAttendeesCapacity() - bookingDto.getSeats().size());

        eventRepository.save(event);

        return savedBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findByIdAndUserEmail(Long id, String userEmail) {
        return bookingRepository.findByIdAndUserEmail(id, userEmail);
    }

    @Override
    public void cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findByIdAndUserEmail(bookingId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        List<Long> seats = booking.getSeats().stream().map(Seat::getId).toList();
        log.info("Booking with id: {} is already cancelled", bookingId);
        booking.setSeats(Collections.emptySet());

        // set status for seats available
        this.seatRepository.updateSeatsStatus(seats, seatStatusRepository.findByName("AVAILABLE").get());

        // Update booking status
        booking.setBookingStatus(bookingStatusRepository.findByName("CANCELLED").get());
        bookingRepository.save(booking);

        // Return tickets to available capacity
        Event event = booking.getEvent();
        event.setAvailableAttendeesCapacity(
                event.getAvailableAttendeesCapacity() + seats.size());

        eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findUserBookings(String userEmail) {
        return bookingRepository.findByUserEmailOrderByBookingDateDesc(userEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> findSeatsByBookingId(Long bookingId) {
        return
                bookingRepository.findSeatsByBooking(bookingId);
    }

    @Override
    public List<Booking> findUserBookingsByStatus(String username, BookingStatus bookingStatus) {
        return this.bookingRepository.findByUserFilteredByStatus(username, bookingStatus.name());
    }

    private BigDecimal calculateTotalPrice(List<SeatDto> seats) {
        return seats.stream()
                .map(SeatDto::getBasePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<Seat> createSeats(@NotNull(message = "Seats are required") List<SeatDto> seats) {
        return seats.stream()
                .map(seatDto -> {
                    Seat seat = new Seat();

                    seat.setRowNumber(seatDto.getRowNumber());
                    seat.setSeatNumber(seatDto.getSeatNumber());
                    seat.setSection(seatDto.getSection());
                    seat.setBasePrice(seatDto.getBasePrice());
                    seat.setIsAvailable(false);

                    return seat;
                })
                .collect(Collectors.toSet());
    }
}
