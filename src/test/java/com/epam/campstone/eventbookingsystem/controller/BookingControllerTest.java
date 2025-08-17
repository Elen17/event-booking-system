package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.BookingDto;
import com.epam.campstone.eventbookingsystem.model.Booking;
import com.epam.campstone.eventbookingsystem.model.Event;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.service.api.BookingService;
import com.epam.campstone.eventbookingsystem.service.api.EventService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;
    private User testUser;
    private Event testEvent;
    private Booking testBooking;
    private BookingDto testBookingDto;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        
        // Setup test event
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setMinPrice(BigDecimal.valueOf(50));
        
        // Setup test booking
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setUser(testUser);
        testBooking.setEvent(testEvent);
        testBooking.setQuantity(2);
        testBooking.setPrice(BigDecimal.valueOf(100));
        
        // Setup test booking DTO
        testBookingDto = new BookingDto();
        testBookingDto.setEventId(1L);
        testBookingDto.setQuantity(2);
        testBookingDto.setPrice(BigDecimal.valueOf(100));
        
        // Setup user details for authentication
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .authorities("USER")
                .build();
    }

    @Test
    void showBookingForm_WhenEventExists_ShouldReturnBookingForm() throws Exception {
        // Given
        when(eventService.findById(1L)).thenReturn(Optional.of(testEvent));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When/Then
        mockMvc.perform(get("/bookings/new/1")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/form"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("booking"))
                .andExpect(model().attributeExists("user"));

        verify(eventService, times(1)).findById(1L);
        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    void showBookingForm_WhenEventNotFound_ShouldRedirectToDashboard() throws Exception {
        // Given
        when(eventService.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/bookings/new/999")
                        .with(user(userDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(eventService, times(1)).findById(999L);
        verify(userService, never()).findByEmail(anyString());
    }

    @Test
    void createBooking_WithValidData_ShouldCreateBooking() throws Exception {
        // Given
        when(eventService.findById(1L)).thenReturn(Optional.of(testEvent));
        when(bookingService.createBooking(anyString(), any(BookingDto.class))).thenReturn(testBooking);

        // When/Then
        mockMvc.perform(post("/bookings")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("ticketQuantity", "2")
                        .flashAttr("booking", testBookingDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(bookingService, times(1)).createBooking(anyString(), any(BookingDto.class));
    }

    @Test
    void createBooking_WithZeroTickets_ShouldShowError() throws Exception {
        // When/Then
        mockMvc.perform(post("/bookings")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("ticketQuantity", "0")
                        .flashAttr("booking", testBookingDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings/new/1"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attributeExists("booking"));

        verify(bookingService, never()).createBooking(anyString(), any(BookingDto.class));
    }

    @Test
    void createBooking_WithInvalidData_ShouldShowValidationErrors() throws Exception {
        // Given
        BookingDto invalidBooking = new BookingDto();
        invalidBooking.setEventId(1L);
        // Missing required fields

        // When/Then
        mockMvc.perform(post("/bookings")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("ticketQuantity", "2")
                        .flashAttr("booking", invalidBooking))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings/new/1"));

        verify(bookingService, never()).createBooking(anyString(), any(BookingDto.class));
    }

    @Test
    void createBooking_WhenEventNotFound_ShouldShowError() throws Exception {
        // Given
        when(eventService.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(post("/bookings")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("ticketQuantity", "2")
                        .flashAttr("booking", testBookingDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings/new/1"));

        verify(bookingService, never()).createBooking(anyString(), any(BookingDto.class));
    }
}
