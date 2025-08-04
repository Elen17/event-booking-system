package com.epam.campstone.eventbookingsystem.repository;

import com.epam.campstone.eventbookingsystem.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByName(String name);
    boolean existsByName(String name);

    default UserRole findByNameOrThrow(String name) {
        return findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("Invalid role name: " + name));
    }
}
