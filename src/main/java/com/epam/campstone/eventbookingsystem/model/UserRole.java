package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_role")
@Getter
public enum UserRole {
    ADMIN("Administrator", "Full system access"),
    CUSTOMER("Customer", "Regular user with booking capabilities"),
    EVENT_MANAGER("Event Manager", "Can create and manage events");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    UserRole(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
