package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.CountryDto;
import com.epam.campstone.eventbookingsystem.dto.UserProfileDto;
import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.model.UserPasswordHistory;
import com.epam.campstone.eventbookingsystem.service.api.BookingService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private User testUser;
    private UserProfileDto testProfileDto;
    private UserDetails userDetails;
    private List<City> cities;
    private List<String> cityNames;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        cities = new ArrayList<>();
        City newYork = Mockito.mock(City.class);
        when(newYork.getName()).thenReturn("New York");
        when(newYork.getId()).thenReturn(1);
        cities.add(newYork);

        City losAngeles = Mockito.mock(City.class);
        when(losAngeles.getName()).thenReturn("Los Angeles");
        when(losAngeles.getId()).thenReturn(2);
        cities.add(losAngeles);

        cityNames = List.of("New York", "Los Angeles");

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        UserPasswordHistory userPasswordHistory = Mockito.mock(UserPasswordHistory.class);
        userPasswordHistory.setPasswordHash("password");
        userPasswordHistory.setUser(testUser);
        testUser.setUserPasswordHistories(Set.of(userPasswordHistory));

        // Setup test profile DTO
        testProfileDto = new UserProfileDto();
        testProfileDto.setEmail("test@example.com");
        testProfileDto.setFirstName("Updated");
        testProfileDto.setLastName("User");
        testProfileDto.setCountry(new CountryDto(1, "New York"));

        // Setup user details for authentication
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .authorities("USER")
                .build();
    }

    @Test
    void showProfile_WhenUserExists_ShouldReturnProfileView() throws Exception {
        // Given
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When/Then
        mockMvc.perform(get("/user/profile")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userProfile"));

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    void showProfile_WhenUserNotFound_ShouldThrowException() throws Exception {
        // Given
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/user/profile")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    void updateProfile_WithValidData_ShouldUpdateProfile() throws Exception {
        // Given

        // When/Then
        mockMvc.perform(post("/user/profile")
                        .with(user(userDetails))
                        .with(csrf())
                        .flashAttr("userProfile", testProfileDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).updateUserProfile(anyString(), any(UserProfileDto.class));
    }

    @Test
    void updateProfile_WithInvalidData_ShouldShowValidationErrors() throws Exception {
        // Given
        UserProfileDto invalidProfile = new UserProfileDto();
        invalidProfile.setEmail(""); // Invalid email

        // When/Then
        mockMvc.perform(post("/user/profile")
                        .with(user(userDetails))
                        .with(csrf())
                        .flashAttr("userProfile", invalidProfile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));

        verify(userService, never()).updateUserProfile(anyString(), any(UserProfileDto.class));
    }

    @Test
    void showChangePasswordForm_ShouldReturnChangePasswordView() throws Exception {
        // When/Then
        mockMvc.perform(get("/user/change-password")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("user/change-password"));
    }

    @Test
    void changePassword_WithMatchingPasswords_ShouldUpdatePassword() throws Exception {
        // Given
        doNothing().when(userService).changePassword(anyString(), anyString(), anyString());

        // When/Then
        mockMvc.perform(post("/user/change-password")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("currentPassword", "oldPassword")
                        .param("newPassword", "newPassword123")
                        .param("confirmPassword", "newPassword123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/change-password"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).changePassword(anyString(), anyString(), anyString());
    }

    @Test
    void changePassword_WithNonMatchingPasswords_ShouldShowError() throws Exception {
        // When/Then
        mockMvc.perform(post("/user/change-password")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("currentPassword", "oldPassword")
                        .param("newPassword", "newPassword123")
                        .param("confirmPassword", "differentPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/change-password"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).changePassword(anyString(), anyString(), anyString());
    }
}
