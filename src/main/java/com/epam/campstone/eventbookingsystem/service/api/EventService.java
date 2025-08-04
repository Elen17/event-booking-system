package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.dto.EventDto;
import com.epam.campstone.eventbookingsystem.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventService {
    /**
     * Find all events with pagination
     *
     * @param pageable pagination information
     * @return page of events
     */
    Page<Event> findAllEvents(Pageable pageable);

    /**
     * Find an event by ID
     *
     * @param id the event ID
     * @return an Optional containing the event if found
     */
    Optional<Event> findById(Long id);

    /**
     * Create a new event
     *
     * @param eventDto the event data
     * @return the created event
     */
    Event createEvent(EventDto eventDto);

    /**
     * Update an existing event
     *
     * @param id       the event ID
     * @param eventDto the updated event data
     * @return the updated event
     */
    Event updateEvent(Long id, EventDto eventDto);

    /**
     * Delete an event by ID
     *
     * @param id the event ID
     */
    void deleteEvent(Long id);

    /**
     * Check if an event has available spots
     *
     * @param eventId the event ID
     * @return true if there are available spots, false otherwise
     */
    boolean hasAvailableSpots(Long eventId);

    /**
     * Decrease available spots for an event
     *
     * @param eventId the event ID
     * @param count   number of spots to decrease
     */
    void decreaseAvailableSpots(Long eventId, int count);

    /**
     * Increase available spots for an event
     *
     * @param eventId the event ID
     * @param count   number of spots to increase
     */
    void increaseAvailableSpots(Long eventId, int count);
}
