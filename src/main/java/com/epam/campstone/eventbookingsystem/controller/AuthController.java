package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.UserRegistrationDto;
import com.epam.campstone.eventbookingsystem.model.Country;
import com.epam.campstone.eventbookingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final CountryService countryService;

    public AuthController(UserService userService, CountryService countryService) {
        this.userService = userService;
        this.countryService = countryService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String logout,
                                @RequestParam(required = false) String registered,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Registration successful! Please log in with your credentials.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("userRegistrationDto")) {
            model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        }
        List<Country> countries = countryService.findAll();
        model.addAttribute("countries", countries);
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userRegistrationDto") @Valid UserRegistrationDto userRegistrationDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        // Check if passwords match
        if (!userRegistrationDto.getPassword().equals(userRegistrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userRegistrationDto", "Passwords do not match");
        }

        // Check if email already exists
        if (userService.existsByEmail(userRegistrationDto.getEmail())) {
            bindingResult.rejectValue("email", "error.userRegistrationDto", "Email is already in use");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.userRegistrationDto",
                    bindingResult);
            redirectAttributes.addFlashAttribute("userRegistrationDto", userRegistrationDto);
            return "redirect:/auth/register";
        }

        try {
            userService.registerNewUser(userRegistrationDto);
            return "redirect:/auth/login?registered";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed. Please try again.");
            return "redirect:/auth/register";
        }
    }
}
