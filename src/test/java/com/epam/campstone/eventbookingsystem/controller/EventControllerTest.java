package com.epam.campstone.eventbookingsystem.controller;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.epam.campstone.eventbookingsystem.handler.GlobalExceptionHandler;
import com.epam.campstone.eventbookingsystem.model.*;
import com.epam.campstone.eventbookingsystem.service.api.CityService;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
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
    private List<String> cityNames;
    private User testUser;
    private Country testCountry;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        initCountries();
        initCities();
        initValidUser();
        initEvents();

        // Setup user details for authentication
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .authorities("USER")
                .build();

        auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());

    }

    @Test
    void listEvents_ShouldReturnPaginatedEvents() throws Exception {
        // Given
        Page<Event> eventPage = new PageImpl<>(events);
        when(eventService.findEvents(any(Pageable.class))).thenReturn(eventPage);

        // When/Then
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/list"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("totalItems", 12L));

        verify(eventService, times(1)).findEvents(any(Pageable.class));
    }

    @Test
    void viewEvent_WhenEventExists_ShouldReturnEventView() throws Exception {
        // Given
        Event event = events.get(0);
        when(eventService.findById(1L)).thenReturn(Optional.of(event));

        // When/Then
        mockMvc.perform(get("/events/1").principal(auth))
                .andExpect(status().isOk())
                .andExpect(view().name("events/view"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attribute("event", event));

        verify(eventService, times(1)).findById(1L);
    }

    @Test
    void viewEvent_WhenEventNotFound_ShouldReturnErrorView() throws Exception {
        // Given
        when(eventService.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/events/999").principal(auth))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/not-found"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(eventService, times(1)).findById(999L);
    }

    @Test
    void searchEvents_WithFilters_ShouldReturnFilteredResults() throws Exception {
        // Given
        Page<Event> eventPage = new PageImpl<>(events);
        when(eventService.searchEvents(any(), any(Pageable.class))).thenReturn(eventPage);
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(cityService.findByCountry(testCountry)).thenReturn(cities);

        // When/Then
        mockMvc.perform(get("/events/search").principal(auth)
                        .param("location", "New York")
                        .param("date", "2025-12-31")
                        .param("category", "CONCERT")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/dashboard"))
                .andExpect(model().attributeExists("featuredEvents"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attribute("cities", cityNames));

        verify(eventService, times(1)).searchEvents(any(), any(Pageable.class));
        verify(cityService, times(1)).findByCountry(testCountry);
    }

    @Test
    void listAllEvents_WithFilters_ShouldReturnFilteredEvents() throws Exception {
        // Given
        Page<Event> eventPage = new PageImpl<>(events);
        when(eventService.searchEvents(any(), any(Pageable.class))).thenReturn(eventPage);
        when(cityService.findByCountry(any())).thenReturn(cities);
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When/Then
        mockMvc.perform(get("/events/all")
                        .principal(auth)
                        .param("category", "CONCERT")
                        .param("location", "1")
                        .param("sortBy", "name")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/dashboard"))
                .andExpect(model().attributeExists("searchResults"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attributeExists("categories"));

//        verify(eventService, times(1)).findByFilters(any(), any(), any(), any(Pageable.class));
//        verify(cityService, times(1)).findAll();
    }

    private void initCountries() {
        testCountry = new Country();
        testCountry.setId(1);
        testCountry.setName("United States");
    }

    private void initEvents() {
        events = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Event event = new Event();
            event.setId((long) RandomUtil.getPositiveInt());
            event.setTitle("Event " + i);
            event.setDescription("Description " + i);
            event.setEventDate(LocalDate.now().plusDays(i));
            event.setStartTime(LocalTime.of(18, 0));
            Venue venue = Mockito.mock(Venue.class);
            venue.setName("Venue " + i);
            venue.setId((long) RandomUtil.getPositiveInt());
            venue.setCity(cities.get(i % 2));

            event.setVenue(venue);
            event.setAvailableAttendeesCapacity(100);
            event.setMinPrice(BigDecimal.valueOf(50L));
            event.setCreatedBy(testUser);
            events.add(event);
        }
        events.add(new Event());
        events.add(new Event());
    }

    private void initCities() {
        cities = new ArrayList<>();
        City newYork = new City();
        newYork.setName("New York");
        newYork.setId(1);
        newYork.setCountry(testCountry);
        cities.add(newYork);

        City losAngeles = new City();
        losAngeles.setName("Los Angeles");
        losAngeles.setId(2);
        losAngeles.setCountry(testCountry);
        cities.add(losAngeles);

        this.cityNames = cities.stream().map(City::getName).collect(Collectors.toList());
    }

    private void initValidUser() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        UserRole userRole = new UserRole();
        userRole.setName("ROLE_USER");
        testUser.setRole(userRole);
        testUser.setCountry(testCountry);
    }
}
