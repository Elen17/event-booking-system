package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.City;
import com.epam.campstone.eventbookingsystem.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByCity(City city);
    List<Venue> findByCityId(Long cityId);
    List<Venue> findByNameContainingIgnoreCase(String name);
    boolean existsByNameAndCityId(String name, Long cityId);

    @Query("SELECT v FROM Venue v WHERE v.city.country.id = :countryId")
    List<Venue> findByCountryId(@Param("countryId") Long countryId);

    @Query("SELECT v FROM Venue v WHERE LOWER(v.name) LIKE LOWER(concat('%', :query, '%')) " +
           "OR LOWER(v.address) LIKE LOWER(concat('%', :query, '%'))")
    List<Venue> searchVenues(@Param("query") String query);
}
