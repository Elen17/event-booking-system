package com.epam.campstone.eventbookingsystem.controller;

import com.epam.campstone.eventbookingsystem.dto.PaymentRequestDto;
import com.epam.campstone.eventbookingsystem.dto.PaymentResponseDto;
import com.epam.campstone.eventbookingsystem.service.api.PaymentService;
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
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Shows the payment form.
     * <p>
     * This method shows the payment form and pre-populates it with the booking details.
     * If the booking is not found, it throws a runtime exception.
     * <p>
     * The method also checks if the booking has already been paid for.
     * If it has been paid, it redirects to the payment status page.
     *
     * @param bookingId   The ID of the booking to pay for.
     * @param currentUser The user performing the action.
     * @param model       The model to add attributes to.
     * @return The view name for the payment form.
     */
    @GetMapping("/new/{bookingId}")
    public String showPaymentForm(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model) {

        log.info("Showing payment form for booking: {} for user: {}", bookingId, currentUser.getUsername());

        if (!model.containsAttribute("paymentRequest")) {
            PaymentRequestDto paymentRequest = new PaymentRequestDto();
            paymentRequest.setBookingId(bookingId);
            model.addAttribute("paymentRequest", paymentRequest);
        }

        PaymentResponseDto paymentStatus = paymentService.getPaymentStatus(bookingId, currentUser.getUsername());
        log.info("Payment status for booking {}: {}", bookingId, paymentStatus);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("paymentStatus", paymentStatus);

        return "payments/payment-form";
    }

    /**
     * Process a payment.
     *
     * <p>This method processes a payment for the given booking ID.
     * It validates the input data, calls the payment service to process the payment,
     * and redirects to the booking page with a success or error message.
     * If the payment fails, it redirects to the payment form with an error message.
     * If an exception occurs, it redirects to the payment form with an error message.
     *
     * @param paymentRequest     The payment request containing the booking ID and card details.
     * @param bindingResult      The binding result to hold validation errors.
     * @param currentUser        The user performing the action.
     * @param redirectAttributes Attributes for flash messages during redirection.
     * @return The view name or redirect URL.
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
