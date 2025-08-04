package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.exception.DuplicateEmailException;
import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.model.UserRole;
import com.epam.campstone.eventbookingsystem.repository.UserRepository;
import com.epam.campstone.eventbookingsystem.repository.UserRoleRepository;
import com.epam.campstone.eventbookingsystem.service.api.CountryService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final CountryService countryService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           CountryService countryService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.countryService = countryService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUser(UserRegistrationDto registrationDto) {
        // Check if email already exists
        if (existsByEmail(registrationDto.getEmail())) {
            throw new DuplicateEmailException("There is already an account registered with that email: " + registrationDto.getEmail());
        }

        // Create new user
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setActive(true); // Set to false if email verification is required

        // Set country if provided
        if (registrationDto.getCountryId() != null) {
            Country country = countryService.findById(registrationDto.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid country ID: " + registrationDto.getCountryId()));
            user.setCountry(country);
        }

        // Assign default role (ROLE_USER)
        UserRole userRole = userRoleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    UserRole newRole = new UserRole();
                    newRole.setName("ROLE_USER");
                    return userRoleRepository.save(newRole);
                });
        user.setRoles(Collections.singleton(userRole));

        // Generate activation token if email verification is required
        // String activationToken = UUID.randomUUID().toString();
        // user.setActivationToken(activationToken);

        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean activateUser(String activationToken) {
        // Implementation for email verification
        // This would typically find a user by activation token and set them to active
        // For now, we'll return true as a placeholder
        return true;
    }
}
