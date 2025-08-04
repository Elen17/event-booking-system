package com.epam.campstone.eventbookingsystem.controller.api;

import com.epam.campstone.eventbookingsystem.dto.LoginRequestDto;
import com.epam.campstone.eventbookingsystem.dto.JwtResponseDto;
import com.epam.campstone.eventbookingsystem.dto.TokenRefreshRequest;
import com.epam.campstone.eventbookingsystem.dto.TokenRefreshResponse;
import com.epam.campstone.eventbookingsystem.exception.TokenRefreshException;
import com.epam.campstone.eventbookingsystem.model.RefreshToken;
import com.epam.campstone.eventbookingsystem.security.jwt.JwtUtils;
import com.epam.campstone.eventbookingsystem.security.services.UserDetailsImpl;
import com.epam.campstone.eventbookingsystem.service.LoginService;
import com.epam.campstone.eventbookingsystem.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for authentication operations.
 * Handles login, logout, and token refresh operations.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final LoginService loginService;

    @Autowired
    public AuthRestController(
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            RefreshTokenService refreshTokenService,
            LoginService loginService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.loginService = loginService;
    }

    /**
     * Authenticate a user and return JWT tokens
     * 
     * @param loginRequest the login request containing email and password
     * @return JWT token and refresh token
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(loginService.authenticateUser(loginRequest));
    }

    /**
     * Refresh an expired JWT token using a valid refresh token
     * 
     * @param request the refresh token request
     * @return new JWT token and refresh token
     */
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    /**
     * Logout a user by invalidating their refresh token
     * 
     * @param request the logout request containing the refresh token
     * @return a success message
     */
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody TokenRefreshRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok("Log out successful!");
    }
}
