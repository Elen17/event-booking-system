package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.BookingDto;
import com.epam.campstone.eventbookingsystem.model.Booking;
import com.epam.campstone.eventbookingsystem.service.EventService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final EventService eventService;

    public BookingController(BookingService bookingService, EventService eventService) {
        this.bookingService = bookingService;
        this.eventService = eventService;
    }

    @GetMapping("/new/{eventId}")
    public String showBookingForm(
            @PathVariable Long eventId,
            @ModelAttribute("booking") BookingDto bookingDto,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {

        // Check if event exists
        eventService.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Set default values for the booking form
        if (bookingDto.getNumberOfTickets() == null) {
            bookingDto.setNumberOfTickets(1);
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
            Booking booking = bookingService.createBooking(currentUser.getUsername(), bookingDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Booking confirmed! Your booking ID is: " + booking.getId());
            return "redirect:/bookings/" + booking.getId();
        } catch (Exception e) {
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
            bookingService.cancelBooking(id, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully");
        } catch (Exception e) {
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

        Booking booking = bookingService.findByIdAndUserEmail(id, currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("Booking not found or access denied"));

        model.addAttribute("booking", booking);
        model.addAttribute("tickets", bookingService.findTicketsByBookingId(id));

        return "bookings/tickets";
    }
}