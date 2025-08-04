package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.model.RefreshToken;

import java.util.Optional;

/**
 * Service interface for managing refresh tokens.
 * Provides methods to create, find, verify, and delete refresh tokens.
 */
public interface RefreshTokenService {
    
    /**
     * Creates a new refresh token for the specified user.
     * If a refresh token already exists for the user, it will be deleted first.
     *
     * @param userId the ID of the user
     * @return the created refresh token
     * @throws RuntimeException if the user is not found
     */
    RefreshToken createRefreshToken(Long userId);

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the token string to search for
     * @return an Optional containing the refresh token if found, empty otherwise
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Verifies if a refresh token is valid and not expired.
     * If the token is expired, it will be deleted and an exception will be thrown.
     *
     * @param token the refresh token to verify
     * @return the verified refresh token
     * @throws TokenRefreshException if the token is expired
     */
    RefreshToken verifyExpiration(RefreshToken token);

    /**
     * Deletes all refresh tokens associated with the specified user.
     *
     * @param userId the ID of the user
     */
    void deleteByUserId(Long userId);
}
