package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.CountryDto;
import com.epam.campstone.eventbookingsystem.dto.EventDto;
import com.epam.campstone.eventbookingsystem.dto.UserProfileDto;
import com.epam.campstone.eventbookingsystem.dto.VenueDto;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/events")
@Slf4j
public class EventModifierController {

    private final EventService eventService;
    private final UserService userService;

    public EventModifierController(EventService eventService,
                                   UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    /**
     * Shows the event creation form.
     * <p>
     * This method populates the model with a new event and
     * returns the view name for the event creation form.
     *
     * @param model the model to add attributes to
     * @return the view name for the event creation form
     */
    @GetMapping("/new")
    public String showCreateEventForm(Model model) {

        if (!model.containsAttribute("event")) {
            model.addAttribute("event", new EventDto());
        }
        return "events/form";
    }

    /**
     * Creates a new event.
     *
     * <p>This method handles the creation of a new event when accessed by an administrator.
     * It uses the provided EventDto to create the event and adds flash attributes to indicate
     * success or failure. On success, redirects to the events list; on failure, redirects back
     * to the event creation form with error messages.</p>
     *
     * @param eventDto           The data transfer object containing event details.
     * @param redirectAttributes Attributes for flash messages during redirection.
     * @return A redirection to the events list on success, or to the event creation form on error.
     */
    @PostMapping
    public String createEvent(
            @ModelAttribute("event") EventDto eventDto,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("Creating new event: {}", eventDto.getTitle());
            eventService.createEvent(eventDto);

            log.info("Event created successfully: {}: {}", eventDto.getId(), eventDto.getTitle());
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");
            return "redirect:/events";
        } catch (Exception e) {
            log.error("Error creating event: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating event: " + e.getMessage());
            redirectAttributes.addFlashAttribute("event", eventDto);
            return "redirect:/events/new";
        }
    }

    /**
     * Shows the event edit form.
     * <p>
     * This method shows the event edit form, pre-populated with the event details.
     * If the event is not found, it throws a runtime exception.
     *
     * @param id    The ID of the event to edit.
     * @param model The model to add attributes to.
     * @return The view name for the event edit form.
     */
    @GetMapping("/{id}/edit")
    public String showEditEventForm(@PathVariable Long id, Model model, Authentication authentication) {

        User user = userService.findByEmail(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with email %s not found", authentication.getName()))
        );
        model.addAttribute("user", user);

        Event event = eventService.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!model.containsAttribute("event")) {
            EventDto eventDto = mapEventDto(event, user);

            model.addAttribute("event", eventDto);
        }

        return "events/form";
    }

    /**
     * Updates an existing event.
     *
     * <p>This method handles the update of an existing event when accessed by an administrator.
     * It uses the provided EventDto to update the event and adds flash attributes to indicate
     * success or failure. On success, redirects to the events list; on failure, redirects back
     * to the event edit form with error messages.</p>
     *
     * @param id                 The ID of the event to update.
     * @param eventDto           The data transfer object containing event details.
     * @param redirectAttributes Attributes for flash messages during redirection.
     * @return A redirection to the events list on success, or to the event edit form on error.
     */
    @PostMapping("/{id}")
    public String updateEvent(
            @PathVariable Long id,
            @ModelAttribute("event") EventDto eventDto,
            RedirectAttributes redirectAttributes) {

        try {
            eventService.updateEvent(id, eventDto);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
            return "redirect:/events/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating event: " + e.getMessage());
            redirectAttributes.addFlashAttribute("event", eventDto);
            return "redirect:/events/" + id + "/edit";
        }
    }


    private static EventDto mapEventDto(Event event, User user) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setTitle(event.getTitle());
        eventDto.setDescription(event.getDescription());
        VenueDto venueDto = new VenueDto();
        venueDto.setId(event.getVenue().getId());
        venueDto.setName(event.getVenue().getName());
        venueDto.setCityId(event.getVenue().getCity().getId());
        eventDto.setVenue(venueDto);
        eventDto.setEventDate(LocalDateTime.of(event.getEventDate(), event.getStartTime()));
        eventDto.setAttendeesCapacity(event.getAvailableAttendeesCapacity());

        if (user != null) {
            mapUserProfileDto(user, eventDto);
        }
        return eventDto;
    }

    private static void mapUserProfileDto(User user, EventDto eventDto) {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setFirstName(user.getFirstName());
        userProfileDto.setLastName(user.getLastName());
        userProfileDto.setEmail(user.getEmail());
        userProfileDto.setCountry(new CountryDto(user.getCountry().getId(), user.getCountry().getName()));
        eventDto.setCreatedBy(userProfileDto);
    }

}
