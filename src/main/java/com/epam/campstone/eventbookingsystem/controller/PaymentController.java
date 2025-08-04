package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.PaymentRequestDto;
import com.epam.campstone.eventbookingsystem.dto.PaymentResponseDto;
import com.epam.campstone.eventbookingsystem.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Show payment form for a booking
     */
    @GetMapping("/new/{bookingId}")
    public String showPaymentForm(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {
        
        if (!model.containsAttribute("paymentRequest")) {
            PaymentRequestDto paymentRequest = new PaymentRequestDto();
            paymentRequest.setBookingId(bookingId);
            model.addAttribute("paymentRequest", paymentRequest);
        }
        
        // Add booking details to the model for display
        PaymentResponseDto paymentStatus = paymentService.getPaymentStatus(bookingId, currentUser.getUsername());
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("paymentStatus", paymentStatus);
        
        return "payments/payment-form";
    }

    /**
     * Process payment for a booking
     */
    @PostMapping
    public String processPayment(
            @ModelAttribute("paymentRequest") @Valid PaymentRequestDto paymentRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.paymentRequest", 
                bindingResult);
            redirectAttributes.addFlashAttribute("paymentRequest", paymentRequest);
            return "redirect:/payments/new/" + paymentRequest.getBookingId();
        }
        
        try {
            PaymentResponseDto response = paymentService.processPayment(paymentRequest, currentUser.getUsername());
            
            if (response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Payment processed successfully! Transaction ID: " + response.getTransactionId());
                return "redirect:/bookings/" + paymentRequest.getBookingId();
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Payment failed: " + response.getMessage());
                return "redirect:/payments/new/" + paymentRequest.getBookingId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error processing payment: " + e.getMessage());
            return "redirect:/payments/new/" + paymentRequest.getBookingId();
        }
    }

    /**
     * Process refund for a booking
     */
    @PostMapping("/refund/{bookingId}")
    public String processRefund(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        try {
            PaymentResponseDto response = paymentService.processRefund(bookingId, currentUser.getUsername());
            
            if (response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage",
                    "Refund processed successfully! Transaction ID: " + response.getTransactionId());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Refund failed: " + response.getMessage());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error processing refund: " + e.getMessage());
        }
        
        return "redirect:/bookings/" + bookingId;
    }

    /**
     * Check payment status for a booking
     */
    @GetMapping("/status/{bookingId}")
    public String checkPaymentStatus(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {
        
        PaymentResponseDto paymentStatus = paymentService.getPaymentStatus(bookingId, currentUser.getUsername());
        model.addAttribute("paymentStatus", paymentStatus);
        model.addAttribute("bookingId", bookingId);
        
        return "payments/payment-status";
    }
}
