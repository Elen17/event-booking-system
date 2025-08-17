package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.CategoryOptionDto;
import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.service.api.CityService;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
public class HomeController {

    private final EventService eventService;
    private final UserService userService;
    private final CityService cityService;

    public HomeController(EventService eventService,
                          UserService userService,
                          CityService cityService) {
        this.eventService = eventService;
        this.userService = userService;
        this.cityService = cityService;
    }

    @GetMapping("/")
    public String home(Model model, RedirectAttributes redirectAttributes,
                       Authentication authentication) {
        log.info("Accessing home page");

        // Add user info if authenticated
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException(
                            String.format("User with email %s not found", authentication.getName())));
            model.addAttribute("user", user);

            // Add common data for all users
            addCommonModelAttributes(model);

            // Add featured events
            if (user.getRole().getName().equals("ROLE_USER")) {
                List<Event> featuredEvents = eventService.getFeaturedEvents(5);
                model.addAttribute("featuredEvents", featuredEvents);
            } else {
                List<Event> createdEvents = eventService.getFeaturedEventsByUser(10, user.getId());
                model.addAttribute("createdEvents", createdEvents);
            }

            log.info("Authenticated user {} accessing home page", user.getEmail());
        } else {
            log.info("Anonymous user accessing home page");
            redirectAttributes.addFlashAttribute("notLoggedIn", "User is not logged in");
            return "redirect:/auth/login";
        }

        return "home/dashboard";
    }

    @GetMapping("/home")
    public String homePage() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            return "redirect:/auth/login";
        }

        User user = userService.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", authentication.getName())));
        model.addAttribute("user", user);

        // Add dashboard-specific data
        addCommonModelAttributes(model);

        // Get user's recent bookings (if any)
        // List<Booking> recentBookings = bookingService.getRecentBookingsByUser(user, 5);
        // model.addAttribute("recentBookings", recentBookings);

        // Get upcoming events

        // Add featured events
        if (user.getRole().getName().equals("USER")) {
            List<Event> featuredEvents = eventService.getFeaturedEvents(5);
            model.addAttribute("upcomingEvents", featuredEvents);

            model.addAttribute("pageTitle", "Dashboard - Ticketo");
            model.addAttribute("featuredTitle", "Recommended for You");
        } else {
            List<Event> createdEvents = eventService.getFeaturedEventsByUser(10, user.getId());
            model.addAttribute("createdEvents", createdEvents);
        }

        log.info("User {} accessing dashboard", user.getEmail());
        return "home/dashboard"; // Same template, different data
    }


    /**
     * Add common model attributes used across multiple pages
     */
    private void addCommonModelAttributes(Model model) {
        User user = (User) model.getAttribute("user");
        // Add available cities for search dropdown
        List<String> cities = this.cityService.findByCountry(Objects.requireNonNull(user).getCountry())
                .stream().map(City::getName)
                .toList();

        model.addAttribute("cities", cities);

        // Add event categories for search dropdown
        List<CategoryOptionDto> categories = this.eventService.getCategoryOptions();
        model.addAttribute("categories", categories);
    }
}
