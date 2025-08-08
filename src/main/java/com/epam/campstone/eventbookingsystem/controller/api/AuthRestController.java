package com.epam.campstone.eventbookingsystem.controller.api;

import com.epam.campstone.eventbookingsystem.dto.JwtResponseDto;
import com.epam.campstone.eventbookingsystem.dto.LoginRequestDto;
import com.epam.campstone.eventbookingsystem.dto.TokenRefreshRequest;
import com.epam.campstone.eventbookingsystem.service.api.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        this.loginService.logoutUser(request.getRefreshToken());
        return ResponseEntity.ok("Log out successful!");
    }
}
