package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.dto.JwtResponseDto;
import com.epam.campstone.eventbookingsystem.dto.LoginRequestDto;
import com.epam.campstone.eventbookingsystem.exception.AuthenticationException;
import com.epam.campstone.eventbookingsystem.exception.UserNotActiveException;
import com.epam.campstone.eventbookingsystem.model.RefreshToken;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.security.UserDetailsImpl;
import com.epam.campstone.eventbookingsystem.security.jwt.JwtUtils;
import com.epam.campstone.eventbookingsystem.service.api.LoginService;
import com.epam.campstone.eventbookingsystem.service.api.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public LoginServiceImpl(
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public JwtResponseDto authenticateUser(LoginRequestDto loginRequest) {
        try {
            // Authenticate user
            log.info("Authenticating user: {}", loginRequest.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if (!userDetails.isAccountNonLocked()) {
                log.error("User account is locked: {}", userDetails.getUsername());
                throw new UserNotActiveException("User account is not active");
            }
            log.info("Generating JWT token for user: {}", userDetails.getUsername());
            String jwt = jwtUtils.generateJwtToken(userDetails);

            // Get user roles
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Generate refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            // Return JWT response
            return new JwtResponseDto(
                    jwt,
                    refreshToken.getToken(),
                    userDetails.getId(),
                    userDetails.getUsername(),
                    roles);

        } catch (org.springframework.security.core.AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new AuthenticationException("Invalid email or password");
        }
    }

    @Override
    public void logoutUser(String refreshToken) {
        this.refreshTokenService.findByToken(refreshToken)
                .map(RefreshToken::getUser)
                .map(User::getId)
                .ifPresent(refreshTokenService::deleteByUserId);
    }
}
