package com.epam.campstone.eventbookingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "password")
@Getter
@Setter
public class Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String salt;

    @Column(name = "hash_algorithm", nullable = false)
    private String hashAlgorithm = "bcrypt";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Business logic methods
    public boolean isExpired() {
        // Passwords might expire after 90 days
        return createdAt.plusDays(90).isBefore(LocalDateTime.now());
    }

    // Helper method for setting the user (for bidirectional relationship)
    public void setUser(User user) {
        this.user = user;
    }
}
