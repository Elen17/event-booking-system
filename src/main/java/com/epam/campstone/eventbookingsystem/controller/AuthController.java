package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.service.api.CountryService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final CountryService countryService;

    public AuthController(UserService userService,
                          CountryService countryService) {
        this.userService = userService;
        this.countryService = countryService;
    }

    /**
     * Shows the login form based on the presence of query parameters.
     * <p>
     * If the {@code error} query parameter is present, an error message is added
     * to the model. If the {@code logout} query parameter is present, a success
     * message is added to the model. If the {@code registered} query parameter is
     * present, a success message is added to the model. The method then returns
     * the login form view.
     *
     * @param error     the error query parameter
     * @param logout    the logout query parameter
     * @param registered the registered query parameter
     * @param model     the model to add attributes to
     * @return the login form view
     */
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String logout,
                                @RequestParam(required = false) String registered,
                                Model model) {
        log.info("Showing login form");

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Registration successful! Please log in with your credentials.");
        }
        log.info("Redirecting to login page: auth/login");
        return "auth/login";
    }

    /**
     * Show the registration form.
     *
     * <p>This method shows the user registration form and populates the model with a new user registration
     * data transfer object and a list of all countries.
     *
     * @param model The model to populate with data.
     * @return The view name for the user registration form.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.info("Showing registration form");

        if (!model.containsAttribute("userRegistrationDto")) {
            model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        }
        List<Country> countries = countryService.findAll();
        log.info("Found {} countries", countries.size());

        model.addAttribute("countries", countries);
        return "auth/register";
    }


    /**
     * Handles user registration by validating input data and creating a new user account.
     *
     * @param userRegistrationDto The user registration data transfer object containing user details.
     * @param bindingResult The binding result to hold validation errors.
     * @param redirectAttributes Attributes for flash messages during redirection.
     * @return A redirection to the registration page if validation fails, or to the login page if registration is successful.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userRegistrationDto") @Valid UserRegistrationDto userRegistrationDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        // Check for passwords match
        if (!userRegistrationDto.getPassword().equals(userRegistrationDto.getConfirmPassword())) {
            log.info("Passwords do not match");
            bindingResult.rejectValue("confirmPassword", "error.userRegistrationDto", "Passwords do not match");
        }

        // Check if email already exists
        if (userService.existsByEmail(userRegistrationDto.getEmail())) {
            log.info("Email is already in use by another user");
            bindingResult.rejectValue("email", "error.userRegistrationDto", "Email is already in use");
        }

        if (bindingResult.hasErrors()) {
            log.info("Validation errors: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.userRegistrationDto",
                    bindingResult);
            redirectAttributes.addFlashAttribute("userRegistrationDto", userRegistrationDto);
            return "redirect:/auth/register";
        }

        try {
            log.info("Registering new user successfully: {}", userRegistrationDto);
            userService.registerNewUser(userRegistrationDto);
            return "redirect:/auth/login?registered";
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed. Please try again.");
            return "redirect:/auth/register";
        }
    }
}
