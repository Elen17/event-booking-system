package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.EventStatus;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface EventStatusRepository extends JpaRepository<EventStatus, Long> {
    /**
    * Find an event status by its name (case-sensitive).
    *
    * @param name the name of the event status to find
    * @return an Optional containing the event status if found, empty otherwise
    * */
    Optional<EventStatus> findByName(String name);

    /**
    * Find all event statuses sorted by name in ascending order.
    *
    * @return a list of all event statuses sorted by name
    * */
    @Override
    @NonNull
    List<EventStatus> findAll();
}
