package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.BookingDto;
import com.epam.campstone.eventbookingsystem.model.Booking;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.service.api.BookingService;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final EventService eventService;
    private final UserService userService;

    public BookingController(BookingService bookingService,
                             EventService eventService,
                             UserService userService) {
        this.bookingService = bookingService;
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/new/{eventId}")
    public String showBookingForm(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {

        log.info("Showing booking form for event: {} by user: {}", eventId, currentUser.getUsername());

        try {
            // Get event details
            Event event = eventService.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            // Get current user details
            Optional<User> user = userService.findByEmail(currentUser.getUsername());

            // Create new booking DTO with pre-filled user data if available
            BookingDto bookingDto = new BookingDto();
            bookingDto.setEventId(eventId);

            // Add data to model
            model.addAttribute("event", event);
            model.addAttribute("booking", bookingDto);
            model.addAttribute("eventId", eventId);
            model.addAttribute("user", user.orElse(null));

            return "booking/form";

        } catch (Exception e) {
            log.error("Error loading booking form for event {}: {}", eventId, e.getMessage());
            model.addAttribute("errorMessage", "Error loading booking form: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PostMapping
    public String createBooking(
            @Valid @ModelAttribute("booking") BookingDto bookingDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(value = "ticketQuantity", defaultValue = "0") int quantity,
            RedirectAttributes redirectAttributes) {

        log.info("Creating booking for user: {}, event: {}, tickets: {}",
                currentUser.getUsername(), bookingDto.getEventId(), quantity);

        try {
            // Validate ticket quantities
            if (quantity == 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one ticket");
                redirectAttributes.addFlashAttribute("booking", bookingDto);
                return "redirect:/bookings/new/" + bookingDto.getEventId();
            }

            // Validate form data
            if (bindingResult.hasErrors()) {
                log.warn("Validation errors in booking form: {}", bindingResult.getAllErrors());
                redirectAttributes.addFlashAttribute("errorMessage", "Please check your information and try again");
                redirectAttributes.addFlashAttribute("booking", bookingDto);
                return "redirect:/bookings/new/" + bookingDto.getEventId();
            }

            // Get event to calculate pricing
            Event event = eventService.findById(bookingDto.getEventId())
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            // Set ticket quantities in DTO
            bookingDto.setQuantity(quantity);



            // Calculate total amount
            // suppose that all tickets have the same price, for simplicity
            BigDecimal totalAmount = event.getMinPrice().multiply(new BigDecimal(quantity));
            bookingDto.setPrice(totalAmount);

            // Create booking
            Booking booking = bookingService.createBooking(currentUser.getUsername(), bookingDto);

            log.info("Booking created successfully: {} with total amount: {}", booking.getId(), totalAmount);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Booking confirmed! Your booking ID is: " + booking.getId());

            return "redirect:/bookings/" + booking.getId();

        } catch (IllegalArgumentException e) {
            log.warn("Invalid booking request: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("booking", bookingDto);
            return "redirect:/bookings/new/" + bookingDto.getEventId();

        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error creating booking: " + e.getMessage());
            redirectAttributes.addFlashAttribute("booking", bookingDto);
            return "redirect:/bookings/new/" + bookingDto.getEventId();
        }
    }

    /*

    todo: Implement later

    @GetMapping("/{id}")
    public String viewBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {

        log.info("Viewing booking: {} for user: {}", id, currentUser.getUsername());

        try {
            Booking booking = bookingService.findByIdAndUserEmail(id, currentUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("Booking not found or access denied"));

            model.addAttribute("booking", booking);
            return "bookings/view";

        } catch (Exception e) {
            log.error("Error viewing booking {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Error loading booking: " + e.getMessage());
            return "redirect:/user/bookings";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        log.info("Canceling booking: {} for user: {}", id, currentUser.getUsername());

        try {
            bookingService.cancelBooking(id, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully");
            log.info("Booking {} cancelled successfully", id);

        } catch (IllegalStateException e) {
            log.warn("Cannot cancel booking {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        } catch (Exception e) {
            log.error("Error cancelling booking {}: {}", id, e.getMessage());
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

        try {
            Booking booking = bookingService.findByIdAndUserEmail(id, currentUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("Booking not found or access denied"));

            // Ensure booking is confirmed before showing tickets
            if (!booking.getBookingStatus().getName().equals("PURCHASED")) {
                model.addAttribute("errorMessage", "Tickets are only available for confirmed bookings");
                return "redirect:/bookings/" + id;
            }

            model.addAttribute("booking", booking);
            return "bookings/tickets";

        } catch (Exception e) {
            log.error("Error viewing tickets for booking {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Error loading tickets: " + e.getMessage());
            return "redirect:/user/bookings";
        }
    }
*/
    /**
     * Handle booking form validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleValidationError(IllegalArgumentException e, RedirectAttributes redirectAttributes) {
        log.warn("Booking validation error: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/dashboard";
    }
}
