package com.epam.campstone.eventbookingsystem.controller.api;

import com.epam.campstone.eventbookingsystem.dto.JwtResponseDto;
import com.epam.campstone.eventbookingsystem.dto.LoginRequestDto;
import com.epam.campstone.eventbookingsystem.dto.TokenRefreshRequest;
import com.epam.campstone.eventbookingsystem.exception.AuthenticationException;
import com.epam.campstone.eventbookingsystem.service.api.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private AuthRestController authRestController;

    MockMvc mockMvc;

    private LoginRequestDto validLoginRequest;
    private JwtResponseDto jwtResponse;
    private TokenRefreshRequest tokenRefreshRequest;


    @BeforeEach
    void setUp() {
        // Setup test data
        validLoginRequest = new LoginRequestDto("user@example.com", "password123");
        jwtResponse = new JwtResponseDto("jwtToken", "refreshToken",
                1L, "user@example.com",
                Collections.singletonList("ROLE_USER"));
        tokenRefreshRequest = new TokenRefreshRequest("refreshToken");

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authRestController).build();

    }

    @Test
    void processLogin_WithValidCredentials_ReturnsJwtResponse() {
        // Arrange
        when(loginService.authenticateUser(any(LoginRequestDto.class))).thenReturn(jwtResponse);

        // Act
        ResponseEntity<JwtResponseDto> response = authRestController.processLogin(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jwtResponse, response.getBody());
        verify(loginService, times(1)).authenticateUser(validLoginRequest);
    }

    @Test
    void processLogin_WithInvalidCredentials_ThrowsException() {
        // Arrange
        when(loginService.authenticateUser(any(LoginRequestDto.class)))
                .thenThrow(new AuthenticationException("Invalid email or password"));

        // Act & Assert
        assertThrows(AuthenticationException.class,
                () -> authRestController.processLogin(validLoginRequest));
        verify(loginService, times(1)).authenticateUser(validLoginRequest);
    }

    @Test
    void processLogin_WithNullRequest_ShouldReturnBadRequest() {
        // Act
        ResponseEntity<JwtResponseDto> response = authRestController.processLogin(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(loginService, never()).authenticateUser(any());
    }

    @Test
    void logoutUser_WithValidToken_ReturnsSuccessMessage() {
        // Arrange
        doNothing().when(loginService).logoutUser(anyString());

        // Act
        ResponseEntity<String> response = authRestController.logoutUser(tokenRefreshRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Log out successful!", response.getBody());
        verify(loginService, times(1)).logoutUser(tokenRefreshRequest.getRefreshToken());
    }

    @Test
    void logoutUser_WithNullRequest_ThrowsException() {
        // Act & Assert
        ResponseEntity<String> badResponse = authRestController.logoutUser(null);

        assertEquals(HttpStatus.BAD_REQUEST, badResponse.getStatusCode());
        assertEquals("Token refresh request is null", badResponse.getBody());
        verify(loginService, never()).logoutUser(anyString());
    }
}