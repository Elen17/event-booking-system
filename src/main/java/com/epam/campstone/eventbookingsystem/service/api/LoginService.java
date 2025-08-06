package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.dto.JwtResponseDto;
import com.epam.campstone.eventbookingsystem.dto.LoginRequestDto;
import com.epam.campstone.eventbookingsystem.exception.AuthenticationException;

public interface LoginService {
    /**
     * Authenticates a user and returns JWT token if successful
     *
     * @param loginRequest The login request containing email and password
     * @return JwtResponse containing the JWT token and user details
     * @throws AuthenticationException if authentication fails
     */
    JwtResponseDto authenticateUser(LoginRequestDto loginRequest);

    void logoutUser(String refreshToken);
}
