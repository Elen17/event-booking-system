package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.dto.UserProfileDto;
import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.model.User;
import jakarta.validation.Valid;

import java.util.Optional;

public interface UserService {

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
     * Update the profile of an existing user with the given details.
     *
     * @param username the username of the user whose profile is to be updated
     * @param userProfile the new profile details to update the user with
     * @throws IllegalArgumentException if the user does not exist or the update fails
     */
    void updateUserProfile(String username, @Valid UserProfileDto userProfile);

    /**
     * Changes the password for a user with the given username and current password
     * to the given new password.
     *
     * @param username the username of the user whose password is to be changed
     * @param currentPassword the current password of the user
     * @param newPassword the new password to set for the user
     * @throws IllegalArgumentException if the user does not exist,
     *         or the current password is incorrect
     *         or the new password has been already used by the user
     */
    void changePassword(String username, String currentPassword, String newPassword);
}
