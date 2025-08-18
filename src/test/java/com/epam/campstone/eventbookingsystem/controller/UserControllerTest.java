package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.CountryDto;
import com.epam.campstone.eventbookingsystem.dto.UserProfileDto;
import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.model.UserRole;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private User testUser;
    private UserProfileDto testProfileDto;
    private Country testCountry;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // Initialize test data
        initCountries();
        initValidUser();
        initUserDto();

        // Setup user details for authentication
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .authorities("USER")
                .build();

        auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());

    }

    @Test
    @WithUserDetails("test@example.com")
    void showProfile_WhenUserExists_ShouldReturnProfileView() throws Exception {
        // Given
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When/Then
        mockMvc.perform(get("/user/profile")
                        .principal(auth)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userProfile"))
                .andExpect(model().attribute("user", testUser))
                .andExpect(model().attribute("userProfile", testProfileDto));

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    void showProfile_WhenUserNotFound_ShouldThrowException() throws Exception {
        // Given
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/user/profile")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(view().name("error/not-found"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(userService, times(1)).findByEmail(anyString());
    }

    private void initCountries() {
        testCountry = new Country();
        testCountry.setId(1);
        testCountry.setName("United States");
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

    private void initUserDto() {
        testProfileDto = new UserProfileDto();
        testProfileDto.setId(1L);
        testProfileDto.setEmail("test@example.com");
        testProfileDto.setFirstName("Test");
        testProfileDto.setLastName("User");
        testProfileDto.setCountry(new CountryDto(1, "United States"));
    }
}
