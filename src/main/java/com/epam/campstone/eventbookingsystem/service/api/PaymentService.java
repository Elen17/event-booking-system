package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.dto.PaymentRequestDto;
import com.epam.campstone.eventbookingsystem.dto.PaymentResponseDto;

public interface PaymentService {
    /**
     * Process a payment for a booking
     * @param paymentRequest The payment request containing payment details
     * @param userEmail The email of the user making the payment
     * @return PaymentResponseDto containing the payment processing result
     */
    PaymentResponseDto processPayment(PaymentRequestDto paymentRequest, String userEmail);

    /**
     * Refund a payment for a booking
     * @param bookingId The ID of the booking to refund
     * @param userEmail The email of the user requesting the refund
     * @return PaymentResponseDto containing the refund processing result
     */
    PaymentResponseDto processRefund(Long bookingId, String userEmail);

    /**
     * Get payment status for a booking
     * @param bookingId The ID of the booking
     * @param userEmail The email of the user requesting the status
     * @return PaymentResponseDto containing the payment status
     */
    PaymentResponseDto getPaymentStatus(Long bookingId, String userEmail);
}
