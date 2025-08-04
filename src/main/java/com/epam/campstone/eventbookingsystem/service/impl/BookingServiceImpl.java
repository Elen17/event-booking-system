package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.dto.BookingDto;
import com.epam.campstone.eventbookingsystem.exception.ResourceNotFoundException;
import com.epam.campstone.eventbookingsystem.model.Booking;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.Ticket;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.repository.BookingRepository;
import com.epam.campstone.eventbookingsystem.repository.EventRepository;
import com.epam.campstone.eventbookingsystem.repository.TicketRepository;
import com.epam.campstone.eventbookingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                            EventRepository eventRepository,
                            UserRepository userRepository,
                            TicketRepository ticketRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Booking createBooking(String userEmail, BookingDto bookingDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        
        Event event = eventRepository.findById(bookingDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + bookingDto.getEventId()));
        
        // Check if there are enough available spots
        if (event.getAvailableAttendeesCapacity() < bookingDto.getNumberOfTickets()) {
            throw new IllegalStateException("Not enough available spots for this event");
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setBookingDate(LocalDateTime.now());
        booking.setNumberOfTickets(bookingDto.getNumberOfTickets());
        booking.setTotalPrice(calculateTotalPrice(event.getTicketPrice(), bookingDto.getNumberOfTickets()));
        booking.setBookingNumber(generateBookingNumber());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Generate tickets
        List<Ticket> tickets = generateTickets(savedBooking, bookingDto.getNumberOfTickets());
        ticketRepository.saveAll(tickets);
        
        // Update available spots
        event.setAvailableAttendeesCapacity(event.getAvailableAttendeesCapacity() - bookingDto.getNumberOfTickets());
        eventRepository.save(event);
        
        return savedBooking;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findByIdAndUserEmail(Long id, String userEmail) {
        return bookingRepository.findByIdAndUserEmail(id, userEmail);
    }

    @Override
    public boolean cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findByIdAndUserEmail(bookingId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            return false; // Already cancelled
        }
        
        // Update booking status
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Return tickets to available capacity
        Event event = booking.getEvent();
        event.setAvailableAttendeesCapacity(
                event.getAvailableAttendeesCapacity() + booking.getNumberOfTickets()
        );
        eventRepository.save(event);
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findUserBookings(String userEmail) {
        return bookingRepository.findByUserEmailOrderByBookingDateDesc(userEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findTicketsByBookingId(Long bookingId) {
        return ticketRepository.findByBookingId(bookingId);
    }
    
    private List<Ticket> generateTickets(Booking booking, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Ticket ticket = new Ticket();
                    ticket.setBooking(booking);
                    ticket.setTicketNumber(generateTicketNumber(booking.getBookingNumber(), i + 1));
                    ticket.setStatus(Ticket.TicketStatus.VALID);
                    return ticket;
                })
                .collect(Collectors.toList());
    }
    
    private String generateBookingNumber() {
        return "BK" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    private String generateTicketNumber(String bookingNumber, int index) {
        return bookingNumber + "-T" + String.format("%03d", index);
    }
    
    private double calculateTotalPrice(double ticketPrice, int numberOfTickets) {
        return ticketPrice * numberOfTickets;
    }
}
