package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.model.Country;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing countries.
 */
public interface CountryService {

    /**
     * Find a country by its ID.
     *
     * @param id the country ID
     * @return an Optional containing the country if found
     */
    Optional<Country> findById(Long id);

    /**
     * Find a country by its name (case-insensitive).
     *
     * @param name the country name
     * @return an Optional containing the country if found
     */
    Optional<Country> findByName(String name);

    /**
     * Find a country by its ISO code (case-insensitive).
     *
     * @param isoCode the ISO country code (2 or 3 letters)
     * @return an Optional containing the country if found
     */
    Optional<Country> findByIsoCode(String isoCode);

    /**
     * Retrieve all countries, ordered by name.
     *
     * @return a list of all countries
     */
    List<Country> findAll();

    /**
     * Save a country.
     *
     * @param country the country to save
     * @return the saved country
     */
    Country save(Country country);

    /**
     * Delete a country by its ID.
     *
     * @param id the country ID
     */
    void deleteById(Long id);

    /**
     * Check if a country with the given ID exists.
     *
     * @param id the country ID
     * @return true if a country with the ID exists, false otherwise
     */
    boolean existsById(Long id);
}
