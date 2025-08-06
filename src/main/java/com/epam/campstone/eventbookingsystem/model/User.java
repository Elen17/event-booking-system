package com.epam.campstone.eventbookingsystem.model;

import com.epam.campstone.eventbookingsystem.util.PasswordUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "password_salt", nullable = false, length = 24) // Base64 encoded 16 bytes
    private String passwordSalt;

    @Column(name = "is_active", nullable = false)
    private boolean active;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserPassword> passwords = new HashSet<>();

    /**
     * Sets the user's password by hashing it with a new salt
     * @param plainPassword The plain text password to hash and store
     */
    public void setPassword(String plainPassword) {
        this.passwordSalt = PasswordUtil.generateSalt();
        this.passwordHash = PasswordUtil.hashPassword(plainPassword, this.passwordSalt);
        
        // Also update the password history
        UserPassword passwordHistory = new UserPassword();
        passwordHistory.setPasswordHash(this.passwordHash);
        passwordHistory.setSalt(this.passwordSalt);
        passwordHistory.setCreatedAt(LocalDateTime.now());
        this.addPassword(passwordHistory);
    }

    /**
     * Verifies if the provided password matches the stored hash
     * @param plainPassword The plain text password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword) {
        return PasswordUtil.verifyPassword(plainPassword, this.passwordHash, this.passwordSalt);
    }

    // Helper methods for bidirectional relationships
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setUser(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setUser(null);
    }

    public void addPassword(UserPassword password) {
        passwords.add(password);
        password.setUser(this);
    }

    public void removePassword(UserPassword password) {
        passwords.remove(password);
        password.setUser(null);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}
