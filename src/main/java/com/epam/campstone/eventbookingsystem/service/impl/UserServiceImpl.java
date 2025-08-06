package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.dto.UserProfileDto;
import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.exception.DuplicateEmailException;
import com.epam.campstone.eventbookingsystem.exception.DuplicatePasswordException;
import com.epam.campstone.eventbookingsystem.exception.WrongPasswordException;
import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.model.UserPassword;
import com.epam.campstone.eventbookingsystem.model.UserRole;
import com.epam.campstone.eventbookingsystem.repository.UserPasswordRepository;
import com.epam.campstone.eventbookingsystem.repository.UserRepository;
import com.epam.campstone.eventbookingsystem.repository.UserRoleRepository;
import com.epam.campstone.eventbookingsystem.service.api.CountryService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import com.epam.campstone.eventbookingsystem.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final UserRoleRepository userRoleRepository;
    private final CountryService countryService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserPasswordRepository userPasswordRepository,
                           UserRoleRepository userRoleRepository,
                           CountryService countryService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userPasswordRepository = userPasswordRepository;
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
                    UserRole newRole = UserRole.CUSTOMER;
                    return userRoleRepository.save(newRole);
                });
        user.setRole(userRole);

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
    public void updateUserProfile(String username, UserProfileDto userProfile) {
        User user = findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(userProfile.getFirstName());
        user.setLastName(userProfile.getLastName());
        user.setCountry(countryService.findById(userProfile.getCountryId()).orElse(null));
    }

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<String> passwords = this.userPasswordRepository.findUserPasswordByUser_Email(user.getEmail())
                .stream()
                .map(UserPassword::getPasswordHash)
                .toList();

        String currentHashedPassword = this.userPasswordRepository.findLatestUserPasswordByEmail(user.getEmail()).getPasswordHash();
        if (!passwordEncoder.matches(currentPassword, currentHashedPassword)) {
            log.error("Current password is incorrect: hashes do not match");
            throw new WrongPasswordException("Current password is incorrect");
        }

        String newHashedPassword = PasswordUtil.hashPassword(newPassword, user.getPasswordSalt());
        if (passwords.contains(newHashedPassword)) {
            log.error("Password has been already used by the user");
            throw new DuplicatePasswordException("Password has been already used by the user");
        }

        log.info("Updating password for user: {}", username);
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
