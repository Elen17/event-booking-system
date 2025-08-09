package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.repository.CityRepository;
import com.epam.campstone.eventbookingsystem.service.api.CityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public Optional<City> findById(Integer id) {
        return this.cityRepository.findById(id);
    }

    @Override
    public List<City> findByCountry(Country country) {
        return this.cityRepository.findByCountry(country);
    }

    @Override
    public City save(City city) {
        return this.cityRepository.save(city);
    }

    @Override
    public void deleteById(Integer id) {
        this.cityRepository.deleteById(id);
    }

    @Override
    public List<String> findAll() {
        return this.cityRepository.findAll()
                .stream().map(City::getName)
                .collect(Collectors.toList());
    }
}
