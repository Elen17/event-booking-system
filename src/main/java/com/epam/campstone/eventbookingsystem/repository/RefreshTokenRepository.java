package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.RefreshToken;
import com.epam.campstone.eventbookingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find a refresh token by its token value
     * @param token the token value to search for
     * @return an Optional containing the refresh token if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find a refresh token by the associated user
     * @param user the user to search for
     * @return an Optional containing the refresh token if found
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * Delete all refresh tokens associated with a user
     * @param user the user whose tokens should be deleted
     * @return the number of tokens deleted
     */
    @Modifying
    int deleteByUser(User user);

    /**
     * Check if a refresh token exists for the given user
     * @param user the user to check
     * @return true if a refresh token exists for the user, false otherwise
     */
    boolean existsByUser(User user);
}
