package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.model.User;

public interface RegistrationService {
    /**
     * Registers a new user with the provided registration details
     * @param registrationDto The user registration data
     * @return The registered user
     * @throws IllegalArgumentException if the email is already in use
     */
    User registerUser(UserRegistrationDto registrationDto);

    /**
     * Verifies a user's email with the provided token
     * @param token The verification token
     * @return true if verification was successful, false otherwise
     */
    boolean verifyEmail(String token);
}
