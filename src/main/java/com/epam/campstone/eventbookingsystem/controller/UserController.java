package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.UserProfileDto;
import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.service.api.BookingService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    public UserController(UserService userService, BookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        log.info("Showing profile for user: {}", currentUser.getUsername());

        User user = userService.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!model.containsAttribute("userProfile")) {
            log.info("Adding user profile to model");
            UserProfileDto userProfile = new UserProfileDto();
            userProfile.setFirstName(user.getFirstName());
            userProfile.setLastName(user.getLastName());
            userProfile.setEmail(user.getEmail());
            model.addAttribute("userProfile", userProfile);
        }

        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @Valid @ModelAttribute("userProfile") UserProfileDto userProfile,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        log.info("Updating profile for user: {}", currentUser.getUsername());

        if (bindingResult.hasErrors()) {
            log.info("Validation errors during profile update: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userProfile", bindingResult);
            redirectAttributes.addFlashAttribute("userProfile", userProfile);
            return "redirect:/user/profile";
        }

        try {
            userService.updateUserProfile(currentUser.getUsername(), userProfile);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }

        return "redirect:/user/profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password and confirm password do not match");
            return "redirect:/user/change-password";
        }

        try {
            userService.changePassword(currentUser.getUsername(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error changing password: " + e.getMessage());
            return "redirect:/user/change-password";
        }

        return "redirect:/user/profile";
    }

    @GetMapping("/bookings")
    public String getUserBookings(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        model.addAttribute("bookings", bookingService.findUserBookings(currentUser.getUsername()));
        return "user/bookings";
    }
}
