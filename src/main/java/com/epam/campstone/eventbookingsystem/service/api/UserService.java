package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.model.User;

import java.util.Optional;

public interface UserService {

    /**
     * Register a new user with the provided registration details
     *
     * @param registrationDto the user registration data
     * @return the registered user
     * @throws IllegalArgumentException if the email is already in use
     */
    User registerNewUser(UserRegistrationDto registrationDto);

    /**
     * Check if a user with the given email exists
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by email
     *
     * @param email the email to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by ID
     *
     * @param id the user ID
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(Long id);

    /**
     * Save or update a user
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Activate a user account
     *
     * @param activationToken the activation token
     * @return true if the account was activated, false otherwise
     */
    boolean activateUser(String activationToken);
}
