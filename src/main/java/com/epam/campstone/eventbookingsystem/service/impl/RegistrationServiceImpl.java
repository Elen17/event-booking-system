package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.exception.DuplicateEmailException;
import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.model.UserPasswordHistory;
import com.epam.campstone.eventbookingsystem.model.UserRole;
import com.epam.campstone.eventbookingsystem.repository.CountryRepository;
import com.epam.campstone.eventbookingsystem.repository.UserPasswordRepository;
import com.epam.campstone.eventbookingsystem.repository.UserRepository;
import com.epam.campstone.eventbookingsystem.repository.UserRoleRepository;
import com.epam.campstone.eventbookingsystem.service.api.RegistrationService;
import com.epam.campstone.eventbookingsystem.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private static final String HASH_ALGORITHM = "bcrypt";

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final CountryRepository countryRepository;
    private final String defaultRoleName;
    private final UserPasswordRepository userPasswordHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            CountryRepository countryRepository,
            @Value("${app.security.default-role:USER}") String defaultRoleName,
            UserPasswordRepository userPasswordHistoryRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.countryRepository = countryRepository;
        this.defaultRoleName = defaultRoleName;
        this.userPasswordHistoryRepository = userPasswordHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        // Check if email is already in use
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new DuplicateEmailException("Email " + registrationDto.getEmail() + " is already in use");
        }

        // Get the default user role
        UserRole userRole = (registrationDto.getIsAdmin() ? userRoleRepository.findByName("ADMIN") : userRoleRepository.findByName(defaultRoleName))
                .orElseThrow(() -> new IllegalStateException("Default user role not found"));

        // Get the country
        Country country = countryRepository.findById(registrationDto.getCountryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid country ID"));

        // Create and save the new user
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setRole(userRole);
        user.setCountry(country);
        user.setCreatedAt(Instant.now());
        user.setIsActive(true);

        // Set the password (this will hash it with a new salt)
        UserPasswordHistory passwordHistory = new UserPasswordHistory();
        String salt = PasswordUtil.generateSalt();
        passwordHistory.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        passwordHistory.setSalt(salt);
        passwordHistory.setCreatedAt(Instant.now());
        passwordHistory.setHashAlgorithm(HASH_ALGORITHM);
        passwordHistory.setUser(user);

        userRepository.save(user);

        userPasswordHistoryRepository.save(passwordHistory);

        user.getUserPasswordHistories().add(passwordHistory);

        return userRepository.save(user);
    }
}
