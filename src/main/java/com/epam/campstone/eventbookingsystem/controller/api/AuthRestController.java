package com.epam.campstone.eventbookingsystem.controller.api;

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
@CrossOrigin(origins = "*", maxAge = 3600)
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
    @PostMapping("/api/auth/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequest,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {

        log.info("Processing login for user: {}", loginRequest.getEmail());

        if (result.hasErrors()) {
            log.warn("Login form validation errors: {}", result.getAllErrors());

            // Re-add model attributes for error display
            model.addAttribute("appName", "Ticketo");
            model.addAttribute("rememberMeEnabled", true);

            return "auth/login";
        }

        try {
            // Here you would typically authenticate the user
            // For now, we'll simulate the authentication process

            // Example authentication logic:
            // User user = authenticationService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            log.info("User {} authenticated successfully", loginRequest.getEmail());

            // Redirect to dashboard or home page after successful login
            return "redirect:/dashboard";

        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getEmail(), e);

            // Add error message and return to login form
            model.addAttribute("error", "Authentication failed. Please try again.");
            model.addAttribute("appName", "Ticketo");
            model.addAttribute("rememberMeEnabled", true);

            return "auth/login";
        }
    }



    /**
     * Logout a user by invalidating their refresh token
     *
     * @param request the logout request containing the refresh token
     * @return a success message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody TokenRefreshRequest request) {
        this.loginService.logoutUser(request.getRefreshToken());
        return ResponseEntity.ok("Log out successful!");
    }
}
