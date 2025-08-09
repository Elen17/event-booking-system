package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.CategoryOptionDto;
import com.epam.campstone.eventbookingsystem.dto.EventSearchDto;
import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.service.api.CityService;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("")
@Slf4j
public class EventController {

    private final EventService eventService;
    private final CityService cityService;
    private final UserService userService;

    public EventController(EventService eventService,
                           CityService cityService,
                           UserService userService) {
        this.eventService = eventService;
        this.cityService = cityService;
        this.userService = userService;
    }

    /**
     * Lists events with pagination and sorting options.
     *
     * <p>This method retrieves a paginated list of events, sorted according to
     * the specified parameters. It adds the list of events and pagination details
     * to the model for rendering in the view.
     *
     * @param page      the page number to retrieve (default is 0)
     * @param size      the number of events per page (default is 10)
     * @param sortBy    the attribute to sort the events by (default is "eventDate")
     * @param direction the sorting direction, either "asc" or "desc" (default is "asc")
     * @param model     the model to add attributes to
     * @return the view name for the events list
     */
    @GetMapping
    public String listEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        log.info("Listing events, page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
        Page<Event> eventsPage = eventService.findEvents(pageable);

        log.info("Found {} events", eventsPage.getTotalElements());

        model.addAttribute("events", eventsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", eventsPage.getTotalPages());
        model.addAttribute("totalItems", eventsPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        return "events/list";
    }

    /**
     * Show the details of an event.
     * <p>
     * This method finds an event by ID and adds it to the model for rendering in the view.
     *
     * @param id    the ID of the event to view
     * @param model the model to add attributes to
     * @return the view name for the event details
     */
    @GetMapping("/{id}")
    public String viewEvent(@PathVariable Long id, Model model) {
        log.info("Viewing event: {}", id);

        Event event = eventService.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        return "events/view";
    }


    @GetMapping("/search")
    public String searchEvents(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            Authentication authentication) {

        log.info("Searching events with params - location: {}, date: {}, category: {}",
                location, date, category);

        // Add user info if authenticated
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            User user = userService.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", authentication.getName())));
            model.addAttribute("user", user);
        }

        // Add common model attributes
        addCommonModelAttributes(model);

        // Create search parameters object
        EventSearchDto searchParams = new EventSearchDto();
        searchParams.setCity(location);
        searchParams.setCategory(category);

        if (date != null && !date.isEmpty()) {
            try {
                searchParams.setDate(LocalDate.parse(date));
            } catch (Exception e) {
                log.warn("Invalid date format: {}", date);
                model.addAttribute("error", "Invalid date format. Please use YYYY-MM-DD format.");
            }
        }

        model.addAttribute("searchParams", searchParams);

        try {
            // Perform search
            Pageable pageable = PageRequest.of(page, size, Sort.by("dateTime").ascending());
            Page<Event> searchResults = eventService.searchEvents(searchParams, pageable);

            model.addAttribute("searchResults", searchResults.getContent());
            model.addAttribute("totalPages", searchResults.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalResults", searchResults.getTotalElements());

            // Still show featured events
            List<Event> featuredEvents = eventService.getFeaturedEvents(4);
            model.addAttribute("featuredEvents", featuredEvents);

            model.addAttribute("pageTitle", "Search Results - Ticketo");
            model.addAttribute("searchTitle", "Search Results");

            log.info("Search returned {} results", searchResults.getTotalElements());

        } catch (Exception e) {
            log.error("Error searching events", e);
            model.addAttribute("error", "An error occurred while searching. Please try again.");

            // Fallback to featured events
            List<Event> featuredEvents = eventService.getFeaturedEvents(6);
            model.addAttribute("featuredEvents", featuredEvents);
        }

        return "home/index";
    }

    @GetMapping("/events")
    public String listAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sortBy,
            Model model,
            Authentication authentication) {

        // Add user info if authenticated
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            User user = userService.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", authentication.getName())));
            model.addAttribute("user", user);
        }

        addCommonModelAttributes(model);

        try {
            // Create sort object
            Sort sort = Sort.by("dateTime").ascending();
            if ("price".equals(sortBy)) {
                sort = Sort.by("minPrice").ascending();
            } else if ("popularity".equals(sortBy)) {
                sort = Sort.by("bookingCount").descending();
            } else if ("date".equals(sortBy)) {
                sort = Sort.by("dateTime").ascending();
            }

            Pageable pageable = PageRequest.of(page, size, sort);

            // Create search criteria
            EventSearchDto searchDto = new EventSearchDto();
            searchDto.setCategory(category);
            searchDto.setCity(location);

            Page<Event> events = eventService.searchEvents(searchDto, pageable);

            model.addAttribute("searchResults", events.getContent());
            model.addAttribute("totalPages", events.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalResults", events.getTotalElements());
            model.addAttribute("pageTitle", "All Events - Ticketo");
            model.addAttribute("featuredTitle", "All Events");

            // Add current filters for the form
            EventSearchDto currentSearch = new EventSearchDto();
            currentSearch.setCategory(category);
            currentSearch.setCity(location);
            model.addAttribute("searchParams", currentSearch);

        } catch (Exception e) {
            log.error("Error loading events", e);
            model.addAttribute("error", "Unable to load events. Please try again later.");
        }

        return "home/index";
    }

    @GetMapping("/api/events/featured")
    public String getFeaturedEventsJson(Model model) {
        // This could be used for AJAX requests if needed
        List<Event> featuredEvents = eventService.getFeaturedEvents(10);
        model.addAttribute("events", featuredEvents);
        return "api/events";
    }


    /**
     * Add common model attributes used across multiple pages
     */
    private void addCommonModelAttributes(Model model) {
        User user = (User) model.getAttribute("user");
        // Add available cities for search dropdown
        List<String> cities = this.cityService.findByCountry(user.getCountry())
                .stream().map(City::getName)
                .toList();

        model.addAttribute("cities", cities);

        // Add event categories for search dropdown
        List<CategoryOptionDto> categories = this.eventService.getCategoryOptions();
        model.addAttribute("categories", categories);
    }


}
