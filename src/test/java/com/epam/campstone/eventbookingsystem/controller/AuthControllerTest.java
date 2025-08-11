package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void showLoginForm_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void showRegistrationForm_ShouldReturnRegistrationView() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("auth/register"));
    }

    @Test
    void registerUser_WithValidData_ShouldRedirectToLogin() throws Exception {
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("password123");
        userDto.setConfirmPassword("password123");

//        when(userService.save(any(UserRegistrationDto.class))).thenReturn(new User());

        mockMvc.perform(post("/auth/register")
                        .param("firstName", userDto.getFirstName())
                        .param("lastName", userDto.getLastName())
                        .param("email", userDto.getEmail())
                        .param("password", userDto.getPassword())
                        .param("confirmPassword", userDto.getConfirmPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?registered"))
                .andExpect(flash().attributeExists("successMessage"));

//        verify(userService, times(1)).save(any(UserRegistrationDto.class));
    }

    @Test
    void registerUser_WithInvalidData_ShouldReturnToRegistrationForm() throws Exception {
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setFirstName(""); // Invalid: empty first name
        userDto.setLastName("Doe");
        userDto.setEmail("invalid-email"); // Invalid email format
        userDto.setPassword("123"); // Too short
        userDto.setConfirmPassword("456"); // Doesn't match

        mockMvc.perform(post("/auth/register")
                        .param("firstName", userDto.getFirstName())
                        .param("lastName", userDto.getLastName())
                        .param("email", userDto.getEmail())
                        .param("password", userDto.getPassword())
                        .param("confirmPassword", userDto.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("user", "firstName"))
                .andExpect(model().attributeHasFieldErrors("user", "email"))
                .andExpect(model().attributeHasFieldErrors("user", "password"));

//        verify(userService, never()).save(any(UserRegistrationDto.class));
    }

    @Test
    void login_WithErrorParam_ShouldAddErrorMessage() throws Exception {
        mockMvc.perform(get("/auth/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(view().name("auth/login"));
    }

    @Test
    void login_WithLogoutParam_ShouldAddSuccessMessage() throws Exception {
        mockMvc.perform(get("/auth/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(view().name("auth/login"));
    }

    @Test
    void login_WithRegisteredParam_ShouldAddSuccessMessage() throws Exception {
        mockMvc.perform(get("/auth/login").param("registered", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(view().name("auth/login"));
    }
}
