package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Country;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing countries.
 */
public interface CityService {

    /**
     * Find a city by its ID.
     *
     * @param id the country ID
     * @return an Optional containing the country if found
     */
    Optional<City> findById(Integer id);

    /**
     * Find cities by country.
     *
     * @param country the country
     * @return a list of cities
     */
    List<City> findByCountry(Country country);

    /**
     * Save a city.
     *
     * @param city the city to save
     * @return the saved city
     */
    City save(City city);

    /**
     * Delete a city by its ID.
     *
     * @param id the city ID
     */
    void deleteById(Integer id);

    /**
     * Retrieve all cities, ordered by name.
     *
     * @return a list of all cities
     */
    List<String> findAll();
}
