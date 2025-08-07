package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Venue entities.
 * Provides methods for querying venues with various filtering options and pagination support.
 */
@Repository
@Transactional(readOnly = true)
public interface VenueRepository extends JpaRepository<Venue, Long> {
    /**
     * Find all venues in a specific city.
     *
     * @param city the city to find venues in
     * @return a list of venues in the specified city
     */
    List<Venue> findByCity(@Param("city") City city);

    /**
     * Find all venues in a specific city with pagination.
     *
     * @param city     the city to find venues in
     * @param pageable pagination and sorting information
     * @return a page of venues in the specified city
     */
    Page<Venue> findByCity(@Param("city") City city, Pageable pageable);

    /**
     * Find venues by name (case-insensitive partial match).
     *
     * @param name the name or part of the name to search for
     * @return a list of matching venues
     */
    List<Venue> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find venues by name (case-insensitive partial match) with pagination.
     *
     * @param name     the name or part of the name to search for
     * @param pageable pagination and sorting information
     * @return a page of matching venues
     */
    Page<Venue> findByNameContainingIgnoreCase(
            @Param("name") String name,
            Pageable pageable
    );

    /**
     * Find all venues in a specific country by country ID.
     *
     * @param countryId the ID of the country to find venues in
     * @return a list of venues in the specified country
     */
    @Query("SELECT v FROM Venue v WHERE v.city.country.id = :countryId")
    List<Venue> findByCountryId(@Param("countryId") Long countryId);

    /**
     * Find all venues in a specific country by country ID with pagination.
     *
     * @param countryId the ID of the country to find venues in
     * @param pageable  pagination and sorting information
     * @return a page of venues in the specified country
     */
    @Query("SELECT v FROM Venue v WHERE v.city.country.id = :countryId")
    Page<Venue> findByCountryId(
            @Param("countryId") Long countryId,
            Pageable pageable
    );

    /**
     * Find a venue by name and city ID.
     *
     * @param name   the name of the venue
     * @param cityId the ID of the city
     * @return an Optional containing the venue if found, empty otherwise
     */
    @Query("SELECT v FROM Venue v WHERE v.name = :name AND v.city.id = :cityId")
    Optional<Venue> findByNameAndCityId(
            @Param("name") String name,
            @Param("cityId") Long cityId
    );

    /**
     * Count the number of venues in a specific city.
     *
     * @param cityId the ID of the city
     * @return the number of venues in the specified city
     */
    @Query("SELECT COUNT(v) FROM Venue v WHERE v.city.id = :cityId")
    long countByCityId(@Param("cityId") Long cityId);

    /**
     * Count the number of venues in a specific country.
     *
     * @param countryId the ID of the country
     * @return the number of venues in the specified country
     */
    @Query("SELECT COUNT(v) FROM Venue v WHERE v.city.country.id = :countryId")
    long countByCountryId(@Param("countryId") Long countryId);

    /**
     * Update a venue's address.
     *
     * @param venueId    the ID of the venue to update
     * @param newAddress the new address
     * @return the number of venues updated (1 if successful, 0 otherwise)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Venue v SET v.address = :newAddress WHERE v.id = :venueId")
    int updateAddress(
            @Param("venueId") Long venueId,
            @Param("newAddress") String newAddress
    );
}
