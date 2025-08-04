package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);
    List<City> findByCountry(Country country);
    List<City> findByCountryId(Long countryId);
    List<City> findByNameContainingIgnoreCase(String name);
    boolean existsByNameAndCountryId(String name, Long countryId);
}
