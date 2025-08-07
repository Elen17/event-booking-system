package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.BookingDto;
import com.epam.campstone.eventbookingsystem.model.Booking;
import com.epam.campstone.eventbookingsystem.service.api.BookingService;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final EventService eventService;

    public BookingController(BookingService bookingService,
                             EventService eventService) {
        this.bookingService = bookingService;
        this.eventService = eventService;
    }

    @GetMapping("/new/{eventId}")
    public String showBookingForm(
            @PathVariable Long eventId,
            @ModelAttribute("booking") BookingDto bookingDto,
            Model model) {

        log.info("Showing booking form for event: {}", eventId);

        // Check if event exists
        eventService.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Set default values for the booking form
        if (bookingDto.getSeats() == null) {
            log.info("Current event has no available tickets");
            model.addAttribute("errorMessage", "Current event has no available tickets");
            return "redirect:/events/" + eventId;
        }

        model.addAttribute("eventId", eventId);
        return "bookings/form";
    }

    @PostMapping
    public String createBooking(
            @ModelAttribute("booking") BookingDto bookingDto,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("Creating booking for user: {}, event: {}", currentUser.getUsername(), bookingDto.getEventId());

            Booking booking = bookingService.createBooking(currentUser.getUsername(), bookingDto);

            log.info("Booking created successfully: {}", booking.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Booking confirmed! Your booking ID is: " + booking.getId());
            return "redirect:/bookings/" + booking.getId();
        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error creating booking: " + e.getMessage());
            redirectAttributes.addFlashAttribute("booking", bookingDto);
            return "redirect:/bookings/new/" + bookingDto.getEventId();
        }
    }

    @GetMapping("/{id}")
    public String viewBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {

        log.info("Viewing booking: {} for user: {}", id, currentUser.getUsername());

        Booking booking = bookingService.findByIdAndUserEmail(id, currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Booking not found or access denied"));

        model.addAttribute("booking", booking);
        return "bookings/view";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("Canceling booking: {} for user: {}", id, currentUser.getUsername());

            bookingService.cancelBooking(id, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully");
        } catch (Exception e) {
            log.error("Error cancelling booking: {}", e.getMessage());

            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error cancelling booking: " + e.getMessage());
        }

        return "redirect:/user/bookings";
    }

    @GetMapping("/{id}/tickets")
    public String viewTickets(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {

        log.info("Viewing tickets for booking: {} for user: {}", id, currentUser.getUsername());

        Booking booking = bookingService.findByIdAndUserEmail(id, currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Booking not found or access denied"));

        model.addAttribute("booking", booking);

        return "bookings/tickets";
    }
}
