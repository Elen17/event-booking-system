package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.service.api.CityService;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private CityService cityService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventController eventController;

    private MockMvc mockMvc;
    private List<Event> events;
    private List<City> cities;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
        
        // Initialize test data
        events = new ArrayList<>();
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Test Event 1");
        events.add(event1);
        
        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Test Event 2");
        events.add(event2);
        
        cities = new ArrayList<>();
//        cities.add(new City(1, "New York"));
//        cities.add(new City(2, "Los Angeles"));
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
    }

    @Test
    void listEvents_ShouldReturnPaginatedEvents() throws Exception {
        // Given
        Page<Event> eventPage = new PageImpl<>(events);
        when(eventService.findEvents(any(Pageable.class))).thenReturn(eventPage);
//        when(cityService.findAll()).thenReturn(cities);
        
        // When/Then
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/list"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("totalItems", 2));
        
        verify(eventService, times(1)).findEvents(any(Pageable.class));
        verify(cityService, times(1)).findAll();
    }

    @Test
    void viewEvent_WhenEventExists_ShouldReturnEventView() throws Exception {
        // Given
        Event event = events.get(0);
        when(eventService.findById(1L)).thenReturn(Optional.of(event));
//        when(cityService.findAll()).thenReturn(cities);
        
        // When/Then
        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/view"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attribute("event", event))
                .andExpect(model().attributeExists("cities"));
        
        verify(eventService, times(1)).findById(1L);
        verify(cityService, times(1)).findAll();
    }

    @Test
    void viewEvent_WhenEventNotFound_ShouldReturnErrorView() throws Exception {
        // Given
        when(eventService.findById(999L)).thenReturn(Optional.empty());
        
        // When/Then
        mockMvc.perform(get("/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
        
        verify(eventService, times(1)).findById(999L);
    }

    @Test
    void searchEvents_WithFilters_ShouldReturnFilteredResults() throws Exception {
        // Given
        Page<Event> eventPage = new PageImpl<>(events);
        when(eventService.searchEvents(any(), any(Pageable.class))).thenReturn(eventPage);
//        when(cityService.findAll()).thenReturn(cities);
        
        // When/Then
        mockMvc.perform(get("/events/search")
                        .param("location", "New York")
                        .param("date", "2025-12-31")
                        .param("category", "CONCERT")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/search"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attributeExists("searchDto"));
        
        verify(eventService, times(1)).searchEvents(any(), any(Pageable.class));
        verify(cityService, times(1)).findAll();
    }

    @Test
    void listAllEvents_WithFilters_ShouldReturnFilteredEvents() throws Exception {
        // Given
        Page<Event> eventPage = new PageImpl<>(events);
//        when(eventService.findByFilters(any(), any(), any(), any(Pageable.class))).thenReturn(eventPage);
//        when(cityService.findAll()).thenReturn(cities);
        
        // When/Then
        mockMvc.perform(get("/events/all")
                        .param("category", "CONCERT")
                        .param("location", "1")
                        .param("sortBy", "name")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/all"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attributeExists("categories"));
        
//        verify(eventService, times(1)).findByFilters(any(), any(), any(), any(Pageable.class));
//        verify(cityService, times(1)).findAll();
    }
}
