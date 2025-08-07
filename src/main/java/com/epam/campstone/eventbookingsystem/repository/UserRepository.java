package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Provides methods for querying users with various filtering options and pagination support.
 */
@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by email (case-sensitive).
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Check if a user with the given email exists (case-sensitive).
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(@Param("email") String email);

    /**
     * Find a user by email (case-insensitive).
     *
     * @param email the email to search for (case-insensitive)
     * @return an Optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Find all users with a specific role.
     *
     * @param role the role to filter by
     * @return a list of users with the specified role
     */
    List<User> findByRole(@Param("role") UserRole role);

    /**
     * Find all users with a specific role with pagination.
     *
     * @param role     the role to filter by
     * @param pageable pagination and sorting information
     * @return a page of users with the specified role
     */
    Page<User> findByRole(@Param("role") UserRole role, Pageable pageable);

    /**
     * Search users by first name, last name, or email (case-insensitive).
     *
     * @param query    the search query
     * @param pageable pagination and sorting information
     * @return a page of users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(concat('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(concat('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(concat('%', :query, '%'))")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

    /**
     * Find all users from a specific country.
     *
     * @param countryId the ID of the country
     * @return a list of users from the specified country
     */
    @Query("SELECT u FROM User u JOIN u.country c WHERE c.id = :countryId")
    List<User> findByCountryId(@Param("countryId") Long countryId);

    /**
     * Find all users from a specific country with pagination.
     *
     * @param countryId the ID of the country
     * @param pageable  pagination and sorting information
     * @return a page of users from the specified country
     */
    @Query("SELECT u FROM User u JOIN u.country c WHERE c.id = :countryId")
    Page<User> findByCountryId(
            @Param("countryId") Long countryId,
            Pageable pageable
    );

    /**
     * Find all administrators.
     *
     * @return a list of all administrators
     */
    @Query("SELECT u FROM User u WHERE u.role.name = 'ADMIN'")
    List<User> findAllAdmins();

    /**
     * Find all administrators with pagination.
     *
     * @param pageable pagination and sorting information
     * @return a page of administrators
     */
    @Query("SELECT u FROM User u WHERE u.role.name = 'ADMIN'")
    Page<User> findAllAdmins(Pageable pageable);

    /**
     * Find all users with pagination and sorting.
     *
     * @param pageable pagination and sorting information
     * @return a page of users
     */
    @Override
    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);

    /**
     * Find all users with the given IDs.
     *
     * @param ids the IDs of the users to find
     * @return a list of users with the given IDs
     */
    @Override
    @NonNull
    List<User> findAllById(@NonNull Iterable<Long> ids);

    /**
     * Count all users with the given role.
     *
     * @param role the role to count users for
     * @return the number of users with the given role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") UserRole role);

    /**
     * Deactivate a user account.
     *
     * @param userId the ID of the user to deactivate
     * @return the number of users updated (1 if successful, 0 otherwise)
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :userId")
    int deactivateUser(@Param("userId") Long userId);

    /**
     * Activate a user account.
     *
     * @param userId the ID of the user to activate
     * @return the number of users updated (1 if successful, 0 otherwise)
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :userId")
    int activateUser(@Param("userId") Long userId);
}
