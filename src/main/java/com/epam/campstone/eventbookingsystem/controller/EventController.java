package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.EventDto;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/events")
@Slf4j
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

/**
 * Lists events with pagination and sorting options.
 *
 * <p>This method retrieves a paginated list of events, sorted according to
 * the specified parameters. It adds the list of events and pagination details
 * to the model for rendering in the view.
 *
 * @param page the page number to retrieve (default is 0)
 * @param size the number of events per page (default is 10)
 * @param sortBy the attribute to sort the events by (default is "eventDate")
 * @param direction the sorting direction, either "asc" or "desc" (default is "asc")
 * @param model the model to add attributes to
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
     * @param id the ID of the event to view
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
}
