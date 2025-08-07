package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.UserPasswordHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing user password history.
 * Provides methods for querying and managing user password history records.
 */
@Repository
@Transactional(readOnly = true)
public interface UserPasswordRepository extends JpaRepository<UserPasswordHistory, Long> {

    /**
     * Finds all passwords associated with the user identified by the given email.
     *
     * @param email the email of the user whose passwords are to be retrieved
     * @return a list of UserPasswordHistory objects associated with the user's email
     */
    @Query("SELECT p FROM UserPasswordHistory p WHERE p.user.email = :email")
    List<UserPasswordHistory> findUserPasswordByUser_Email(@Param("email") String email);

    /**
     * Finds all passwords associated with the user identified by the given email with pagination.
     *
     * @param email    the email of the user whose passwords are to be retrieved
     * @param pageable pagination and sorting information
     * @return a page of UserPasswordHistory objects associated with the user's email
     */
    @Query("SELECT p FROM UserPasswordHistory p WHERE p.user.email = :email")
    Page<UserPasswordHistory> findUserPasswordByUser_Email(
            @Param("email") String email,
            Pageable pageable
    );

    /**
     * Finds the latest password associated with the user identified by the given email.
     *
     * @param email the email of the user whose latest password is to be retrieved
     * @return an Optional containing the latest UserPasswordHistory object if found
     */
    @Query("SELECT p FROM UserPasswordHistory p WHERE p.user.email = :email ORDER BY p.createdAt DESC")
    Optional<UserPasswordHistory> findLatestUserPasswordByEmail(@Param("email") String email);

    /**
     * Finds the most recent passwords for a user, ordered by creation date (newest first).
     *
     * @param email the email of the user
     * @param limit maximum number of recent passwords to return
     * @return a list of the most recent UserPasswordHistory objects
     */
    @Query("SELECT p FROM UserPasswordHistory p WHERE p.user.email = :email ORDER BY p.createdAt DESC")
    List<UserPasswordHistory> findRecentUserPasswords(
            @Param("email") String email,
            @Param("limit") int limit
    );

    /**
     * Checks if a password has been used by the user before within a certain time period.
     *
     * @param email        the email of the user
     * @param passwordHash the password hash to check
     * @param since        the start date to check from
     * @return true if the password has been used before, false otherwise
     */
    @Query("SELECT COUNT(p) > 0 FROM UserPasswordHistory p WHERE p.user.email = :email " +
            "AND p.passwordHash = :passwordHash AND p.createdAt >= :since")
    boolean hasUsedPassword(
            @Param("email") String email,
            @Param("passwordHash") String passwordHash,
            @Param("since") LocalDateTime since
    );

    /**
     * Finds all password history entries for a user, ordered by creation date (newest first).
     *
     * @param email the email of the user
     * @return a list of password history entries
     */
    @Query("SELECT p FROM UserPasswordHistory p WHERE p.user.email = :email ORDER BY p.createdAt DESC")
    List<UserPasswordHistory> findAllByUserEmailOrderByCreatedAtDesc(@Param("email") String email);
}
