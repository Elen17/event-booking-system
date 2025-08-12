package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.dto.UserProfileDto;
import com.epam.campstone.eventbookingsystem.exception.DuplicatePasswordException;
import com.epam.campstone.eventbookingsystem.exception.WrongPasswordException;
import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.model.UserPasswordHistory;
import com.epam.campstone.eventbookingsystem.repository.UserPasswordRepository;
import com.epam.campstone.eventbookingsystem.repository.UserRepository;
import com.epam.campstone.eventbookingsystem.repository.UserRoleRepository;
import com.epam.campstone.eventbookingsystem.service.api.CountryService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import com.epam.campstone.eventbookingsystem.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final CountryService countryService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserPasswordRepository userPasswordRepository,
                           UserRoleRepository userRoleRepository,
                           CountryService countryService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userPasswordRepository = userPasswordRepository;
        this.countryService = countryService;
        this.passwordEncoder = passwordEncoder;
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
        Country country = countryService.findById(userProfile.getCountry().getId()).orElse(null);
        user.setCountry(country);
    }

    @Transactional
    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<String> passwords = this.userPasswordRepository.findUserPasswordByUser_Email(user.getEmail())
                .stream()
                .map(UserPasswordHistory::getPasswordHash)
                .toList();

        String currentHashedPassword = this.userPasswordRepository
                .findLatestUserPasswordByEmail(user.getEmail()).get().getPasswordHash();

        if (!passwordEncoder.matches(currentPassword, currentHashedPassword)) {
            log.error("Current password is incorrect: hashes do not match");
            throw new WrongPasswordException("Current password is incorrect");
        }

        String newSalt = PasswordUtil.generateSalt();
        String newHashedPassword = PasswordUtil.hashPassword(newPassword, newSalt);
        if (passwords.contains(newHashedPassword)) {
            log.error("Password has been already used by the user");
            throw new DuplicatePasswordException("Password has been already used by the user");
        }

        log.info("Updating password for user: {}", username);
        UserPasswordHistory passwordHistory = new UserPasswordHistory();
        passwordHistory.setPasswordHash(newHashedPassword);
        passwordHistory.setUser(user);
        passwordHistory.setSalt(newSalt);
        passwordHistory.setCreatedAt(Instant.now());
        passwordHistory.setUser(user);

        this.userPasswordRepository.save(passwordHistory);

        user.getUserPasswordHistories().add(passwordHistory);
    }
}
