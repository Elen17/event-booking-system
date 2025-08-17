package com.epam.campstone.eventbookingsystem.controller;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.epam.campstone.eventbookingsystem.model.*;
import com.epam.campstone.eventbookingsystem.service.api.CityService;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import com.epam.campstone.eventbookingsystem.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private CityService cityService;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;
    private List<Event> events;
    private List<City> cities;
    private User testUser;
    private Country testCountry;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

        // Initialize test data
        initCountries();
        initCities();
        initValidUser();
        initEvents();
    }

    private void initCountries() {
        testCountry = new Country();
        testCountry.setId(1);
        testCountry.setName("United States");
    }

    @Test
    void home_WhenNotAuthenticated_ShouldReturnToLoginPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/login"));

        verify(eventService, never()).getFeaturedEventsByUser(anyInt(), anyLong());
        verify(cityService, never()).findAll();
        verify(userService, never()).findByEmail(anyString());
    }

    @Test
    void home_WhenAuthenticated_ShouldIncludeUserInfo() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(testUser));
        when(eventService.getFeaturedEvents(anyInt())).thenReturn(events);
        when(cityService.findByCountry(testCountry)).thenReturn(cities);
        try (MockedStatic<PasswordUtil> passwordUtilMock = Mockito.mockStatic(PasswordUtil.class)) {

            passwordUtilMock.when(() -> PasswordUtil.hashPassword(anyString(), anyString()))
                    .thenReturn("password");

            passwordUtilMock.when(() -> PasswordUtil.verifyPassword(anyString(), anyString(), anyString()))
                    .thenReturn(true);

            Authentication auth = new UsernamePasswordAuthenticationToken("test@example.com", "password", Collections.emptyList());
//            auth.setAuthenticated(true);

            mockMvc.perform(get("/").principal(auth))
                    .andExpect(status().isOk())
                    .andExpect(view().name("home/dashboard"))
                    .andExpect(model().attributeExists("featuredEvents"))
                    .andExpect(model().attributeExists("cities"))
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attribute("user", testUser));

            verify(userService, times(1)).findByEmail("test@example.com");
            verify(eventService, times(1)).getFeaturedEvents(anyInt());
            verify(cityService, times(1)).findByCountry(testCountry);
        }
    }

    @Test
    void home_WhenUserNotFound_ShouldHandleException() {
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(java.util.Optional.empty());

        Authentication auth = new UsernamePasswordAuthenticationToken("nonexistent@example.com", "password", Collections.emptyList());

        Exception exception = assertThrows(Exception.class, () ->
                mockMvc.perform(get("/").principal(auth))
        );

        // Check if the cause is UsernameNotFoundException
        assertInstanceOf(UsernameNotFoundException.class, exception.getCause());
        assertEquals("User with email nonexistent@example.com not found",
                exception.getCause().getMessage());

        verify(userService, times(1)).findByEmail("nonexistent@example.com");
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
        City newYork = Mockito.mock(City.class);
        newYork.setName("New York");
        newYork.setId(1);
        newYork.setCountry(testCountry);
        cities.add(newYork);

        City losAngeles = Mockito.mock(City.class);
        losAngeles.setName("Los Angeles");
        losAngeles.setId(2);
        losAngeles.setCountry(testCountry);
        cities.add(losAngeles);
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
