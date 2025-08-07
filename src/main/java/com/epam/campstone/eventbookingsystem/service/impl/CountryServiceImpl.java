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
    public Optional<Country> findById(Integer id) {
        return countryRepository.findById(id);
    }

    @Override
    public Optional<Country> findByName(String name) {
        return countryRepository.findByName(name);
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
    public void deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Country ID cannot be null");
        }
        countryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && countryRepository.existsById(id);
    }

}
