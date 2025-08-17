package com.epam.campstone.eventbookingsystem.controller.api;

import com.epam.campstone.eventbookingsystem.dto.JwtResponseDto;
import com.epam.campstone.eventbookingsystem.dto.LoginRequestDto;
import com.epam.campstone.eventbookingsystem.dto.TokenRefreshRequest;
import com.epam.campstone.eventbookingsystem.service.api.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Handles login, logout operations.
 */
@CrossOrigin(origins = "/api/auth", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthRestController {
    private final LoginService loginService;

    @Autowired
    public AuthRestController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Authenticate a user and return JWT tokens
     *
     * @param loginRequest the login request containing email and password
     * @return JWT token and refresh token
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> processLogin(@Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequest) {
        if (loginRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(loginService.authenticateUser(loginRequest));
    }


    /**
     * Logout a user by invalidating their refresh token
     *
     * @param request the logout request containing the refresh token
     * @return a success message
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@Valid @RequestBody TokenRefreshRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body("Token refresh request is null");
        }
        this.loginService.logoutUser(request.getRefreshToken());
        return ResponseEntity.ok("Log out successful!");
    }
}
