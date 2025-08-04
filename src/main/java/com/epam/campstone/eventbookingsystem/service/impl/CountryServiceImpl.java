package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.repository.CountryRepository;
import com.epam.campstone.eventbookingsystem.service.api.CountryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Optional<Country> findById(Long id) {
        return countryRepository.findById(id);
    }

    @Override
    public Optional<Country> findByName(String name) {
        return countryRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<Country> findAll() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional
    public Country save(Country country) {
        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null");
        }
        return countryRepository.save(country);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Country ID cannot be null");
        }
        countryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && countryRepository.existsById(id);
    }

    @Override
    @Transactional
    public void initializeCountries() {
        // This method can be used to populate the database with initial country data
        // It's typically called during application startup or as part of a data migration
        if (countryRepository.count() == 0) {
            // Add some sample countries
            saveCountry("United States", "US", "USA");
            saveCountry("United Kingdom", "GB", "GBR");
            saveCountry("Canada", "CA", "CAN");
            saveCountry("Australia", "AU", "AUS");
            saveCountry("Germany", "DE", "DEU");
            saveCountry("France", "FR", "FRA");
            saveCountry("Japan", "JP", "JPN");
            saveCountry("China", "CN", "CHN");
            saveCountry("India", "IN", "IND");
            saveCountry("Brazil", "BR", "BRA");
        }
    }

    private void saveCountry(String name, String iso2Code, String iso3Code) {
        Country country = new Country();
        country.setName(name);
        countryRepository.save(country);
    }
}
