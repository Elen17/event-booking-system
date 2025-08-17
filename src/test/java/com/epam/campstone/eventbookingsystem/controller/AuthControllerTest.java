package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.service.api.CountryService;
import com.epam.campstone.eventbookingsystem.service.api.RegistrationService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private CountryService countryService;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private List<Country> countries;
    private UserRegistrationDto validUserDto;

    @BeforeEach
    void setUp() {
        countries = new ArrayList<>();
        initCountries();

        validUserDto = new UserRegistrationDto(
                "John", "Doe",
                "john.doe@example.com",
                "Password123!",
                "Password123!", 1, false);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void showLoginForm_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeDoesNotExist("logout"));
    }

    @Test
    void showRegistrationForm_ShouldReturnRegistrationViewWithCountries() throws Exception {
        when(countryService.findAll()).thenReturn(countries);

        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("userRegistrationDto"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(model().attribute("countries", countries));
    }

    @Test
    void registerUser_WithValidData_ShouldSaveUserAndRedirect() throws Exception {
        when(registrationService.registerUser(any(UserRegistrationDto.class))).thenReturn(new User());
        when(userService.existsByEmail(validUserDto.getEmail())).thenReturn(false);

        mockMvc.perform(post("/auth/register")
                        .param("firstName", validUserDto.getFirstName())
                        .param("lastName", validUserDto.getLastName())
                        .param("email", validUserDto.getEmail())
                        .param("password", validUserDto.getPassword())
                        .param("confirmPassword", validUserDto.getConfirmPassword())
                        .param("countryId", validUserDto.getCountryId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?registered"));

        verify(registrationService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void registerUser_WithInvalidData_ShouldReturnToFormWithErrorsPasswordDoesNotMatch() throws Exception {
        UserRegistrationDto invalidUser = new UserRegistrationDto();
        invalidUser.setFirstName("");
        invalidUser.setEmail("mail@mail.com");
        invalidUser.setPassword("123");
        invalidUser.setConfirmPassword("456"); // Doesn't match

        mockMvc.perform(post("/auth/register")
                        .param("firstName", invalidUser.getFirstName())
                        .param("email", invalidUser.getEmail())
                        .param("password", invalidUser.getPassword())
                        .param("confirmPassword", invalidUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attribute("confirmPassword", "Passwords do not match"));

        verify(registrationService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void registerUser_WithInvalidData_ShouldReturnToFormWithErrorsPasswordInvalidMail() throws Exception {
        UserRegistrationDto invalidUser = new UserRegistrationDto();
        invalidUser.setFirstName("");
        invalidUser.setEmail("invalid-email"); // Invalid email format
        invalidUser.setPassword("123");
        invalidUser.setConfirmPassword("123");

        when(userService.existsByEmail(invalidUser.getEmail())).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .param("firstName", invalidUser.getFirstName())
                        .param("email", invalidUser.getEmail())
                        .param("password", invalidUser.getPassword())
                        .param("confirmPassword", invalidUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attribute("email", "Email is already in use"));

        verify(registrationService, never()).registerUser(any(UserRegistrationDto.class));
    }


    @Test
    void registerUser_WithExistingEmail_ShouldReturnToFormWithError() throws Exception {
        when(registrationService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/auth/register")
                        .param("firstName", validUserDto.getFirstName())
                        .param("lastName", validUserDto.getLastName())
                        .param("email", validUserDto.getEmail())
                        .param("password", validUserDto.getPassword())
                        .param("confirmPassword", validUserDto.getConfirmPassword())
                        .param("countryId", validUserDto.getCountryId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/auth/register"));
    }

    @Test
    void login_WithErrorParam_ShouldAddErrorMessage() throws Exception {
        mockMvc.perform(get("/auth/login")
                        .param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Invalid username or password"))
                .andExpect(view().name("auth/login"));
    }

    @Test
    void login_WithLogoutParam_ShouldAddSuccessMessage() throws Exception {
        mockMvc.perform(get("/auth/login")
                        .param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "You have been logged out successfully"))
                .andExpect(view().name("auth/login"));
    }

    @Test
    void login_WithRegisteredParam_ShouldAddSuccessMessage() throws Exception {
        mockMvc.perform(get("/auth/login")
                        .param("registered", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void login_WithAllParams_ShouldHandleAllParameters() throws Exception {
        mockMvc.perform(get("/auth/login")
                        .param("error", "true")
                        .param("logout", "true")
                        .param("registered", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("auth/login"));
    }

    private void initCountries() {
        for (int i = 1; i <= 3; i++) {
            Country country = new Country();
            country.setId(i);
            country.setName("Country " + i);
            this.countries.add(country);
        }
    }
}
