package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.dto.EventDto;
import com.epam.campstone.eventbookingsystem.exception.ResourceNotFoundException;
import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.Venue;
import com.epam.campstone.eventbookingsystem.repository.CityRepository;
import com.epam.campstone.eventbookingsystem.repository.EventRepository;
import com.epam.campstone.eventbookingsystem.repository.VenueRepository;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CityRepository cityRepository;
    private final VenueRepository venueRepository;

    public EventServiceImpl(EventRepository eventRepository,
                            CityRepository cityRepository,
                            VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
        this.cityRepository = cityRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Event> findEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public void createEvent(EventDto eventDto) {
        Event event = new Event();
        mapDtoToEntity(eventDto, event);
        this.venueRepository.save(event.getVenue());

        eventRepository.save(event);
    }

    @Override
    public void updateEvent(Long id, EventDto eventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        mapDtoToEntity(eventDto, event);
        eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAvailableSpots(Long eventId) {
        return eventRepository.findById(eventId)
                .map(event -> event.getAvailableAttendeesCapacity() > 0)
                .orElse(false);
    }

    @Override
    @Transactional
    public void decreaseAvailableSpots(Long eventId, int count) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        if (event.getAvailableAttendeesCapacity() < count) {
            throw new IllegalStateException("Not enough available spots");
        }
        Integer availableSpots = Math.max((event.getAvailableAttendeesCapacity() - count), 0);
        event.setAvailableAttendeesCapacity(availableSpots);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void increaseAvailableSpots(Long eventId, int count) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        event.setAvailableAttendeesCapacity(event.getAvailableAttendeesCapacity() + count);
        eventRepository.save(event);
    }

    private void mapDtoToEntity(EventDto dto, Event entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setEventDate(dto.getEventDate().toLocalDate());
        entity.setStartTime(dto.getEventDate().toLocalTime());

        Venue venue = new Venue();
        venue.setName(dto.getVenue().getName());
        venue.setAddress(dto.getVenue().getAddress());
        City city = cityRepository.findById(dto.getVenue().getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + dto.getVenue().getCityId()));

        venue.setCity(city);
        venue.setName(dto.getVenue().getName());

        entity.setVenue(venue);
        entity.setAvailableAttendeesCapacity(dto.getAvailableAttendeesCapacity());
    }
}
