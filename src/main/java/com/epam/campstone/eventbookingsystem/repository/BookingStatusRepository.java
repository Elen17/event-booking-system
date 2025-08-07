package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link BookingStatus} entities.
 * Provides methods to interact with booking status data in the database.
 */
@Repository
@Transactional(readOnly = true)
public interface BookingStatusRepository extends JpaRepository<BookingStatus, Long> {

    /**
     * Find a booking status by its name (case-sensitive).
     *
     * @param name the name of the booking status to find
     * @return an Optional containing the booking status if found, empty otherwise
     */
    Optional<BookingStatus> findByName(@NonNull String name);

    /**
     * Find all booking statuses sorted by name in ascending order.
     *
     * @return a list of all booking statuses sorted by name
     */
    @NonNull
    List<BookingStatus> findAll();

    /**
     * Find all booking statuses with pagination.
     *
     * @param pageable pagination and sorting information
     * @return a page of booking statuses
     */
    @NonNull
    Page<BookingStatus> findAll(@NonNull Pageable pageable);

    /**
     * Find all booking statuses sorted by the given sort criteria.
     *
     * @param sort the sort criteria
     * @return a list of booking statuses sorted by the given criteria
     */
    @NonNull
    List<BookingStatus> findAll(@NonNull Sort sort);

    /**
     * Count all booking statuses.
     *
     * @return the total number of booking statuses
     */
    @Override
    long count();

    /**
     * Check if a booking status with the given ID exists.
     *
     * @param id the ID to check
     * @return true if a booking status with the given ID exists, false otherwise
     */
    @Override
    boolean existsById(@NonNull Long id);
}
