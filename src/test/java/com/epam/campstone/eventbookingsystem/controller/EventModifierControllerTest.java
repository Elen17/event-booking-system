package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.CategoryOptionDto;
import com.epam.campstone.eventbookingsystem.dto.EventDto;
import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.model.Venue;
import com.epam.campstone.eventbookingsystem.service.api.CityService;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventModifierControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private CityService cityService;

    @InjectMocks
    private EventModifierController eventModifierController;

    private MockMvc mockMvc;
    private User testUser;
    private Event testEvent;
    private EventDto testEventDto;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventModifierController).build();

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("organizer@example.com");
        testUser.setFirstName("Event");
        testUser.setLastName("Organizer");

        // Setup test event
        Venue venue = new Venue();
        venue.setId(1L);
        venue.setName("Test Venue");
        
        City city = new City();
        city.setId(1);
        city.setName("Test City");
        venue.setCity(city);
        
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setEventDate(LocalDate.now().plusDays(7));
        testEvent.setStartTime(LocalTime.of(18, 0));
        testEvent.setVenue(venue);
        testEvent.setAvailableAttendeesCapacity(100);

        // Setup test event DTO
        testEventDto = new EventDto();
        testEventDto.setId(1L);
        testEventDto.setTitle("Test Event");
        testEventDto.setDescription("Test Description");
        testEventDto.setEventDate(LocalDateTime.now().plusDays(7));
        testEventDto.setAttendeesCapacity(100);

        // Setup authentication
        authentication = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(), 
                "password"
        );
    }

    @Test
    void showCreateEventForm_ShouldReturnEventForm() throws Exception {
        // Given
        when(userService.findByEmail(anyString())).thenReturn(java.util.Optional.of(testUser));
        when(cityService.findByCountry(any())).thenReturn(List.of(new City()));
        when(eventService.getCategoryOptions()).thenReturn(List.of(new CategoryOptionDto()));

        // When/Then
        mockMvc.perform(get("/events/new")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("events/form"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attributeExists("categories"));

        verify(userService, times(1)).findByEmail(anyString());
        verify(cityService, times(1)).findByCountry(any());
        verify(eventService, times(1)).getCategoryOptions();
    }

    @Test
    void createEvent_WithValidData_ShouldCreateEvent() throws Exception {
        // Given
        when(userService.findByEmail(anyString())).thenReturn(java.util.Optional.of(testUser));
        doNothing().when(eventService).createEvent(any(EventDto.class));

        // When/Then
        mockMvc.perform(post("/events")
                        .principal(authentication)
                        .with(csrf())
                        .flashAttr("event", testEventDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(eventService, times(1)).createEvent(any(EventDto.class));
    }

    @Test
    void showEditEventForm_WhenEventExists_ShouldReturnEditForm() throws Exception {
        // Given
        when(userService.findByEmail(anyString())).thenReturn(java.util.Optional.of(testUser));
        when(eventService.findById(1L)).thenReturn(java.util.Optional.of(testEvent));
        when(cityService.findByCountry(any())).thenReturn(List.of(new City()));
        when(eventService.getCategoryOptions()).thenReturn(List.of(new CategoryOptionDto()));

        // When/Then
        mockMvc.perform(get("/events/1/edit")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("events/form"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("cities"))
                .andExpect(model().attributeExists("categories"));

        verify(eventService, times(1)).findById(1L);
        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    void showEditEventForm_WhenEventNotFound_ShouldThrowException() throws Exception {
        // Given
        when(userService.findByEmail(anyString())).thenReturn(java.util.Optional.of(testUser));
        when(eventService.findById(999L)).thenReturn(java.util.Optional.empty());

        // When/Then
        mockMvc.perform(get("/events/999/edit")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(eventService, times(1)).findById(999L);
    }

    @Test
    void updateEvent_WithValidData_ShouldUpdateEvent() throws Exception {
        // Given
        doNothing().when(eventService).updateEvent(anyLong(), any(EventDto.class));

        // When/Then
        mockMvc.perform(post("/events/1")
                        .with(csrf())
                        .flashAttr("event", testEventDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(eventService, times(1)).updateEvent(anyLong(), any(EventDto.class));
    }

    @Test
    void updateEvent_WithError_ShouldRedirectWithErrorMessage() throws Exception {
        // Given
        doThrow(new RuntimeException("Update failed")).when(eventService).updateEvent(anyLong(), any(EventDto.class));

        // When/Then
        mockMvc.perform(post("/events/1")
                        .with(csrf())
                        .flashAttr("event", testEventDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/1/edit"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attributeExists("event"));

        verify(eventService, times(1)).updateEvent(anyLong(), any(EventDto.class));
    }
}
