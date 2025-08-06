package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.dto.PaymentRequestDto;
import com.epam.campstone.eventbookingsystem.dto.PaymentResponseDto;
import com.epam.campstone.eventbookingsystem.repository.BookingRepository;
import com.epam.campstone.eventbookingsystem.service.api.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final Random random = new Random();

    public PaymentServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequest, String userEmail) {
        // Simulate payment processing with 90% success rate
        boolean isSuccess = random.nextDouble() < 0.9;
        String transactionId = "TXN" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        
        return bookingRepository.findById(paymentRequest.getBookingId())
                .map(booking -> {
                    if (!booking.getUser().getEmail().equals(userEmail)) {
                        return new PaymentResponseDto(
                                false,
                                "",
                                "Unauthorized access to booking",
                                LocalDateTime.now(),
                                paymentRequest.getBookingId(),
                                paymentRequest.getAmount(),
                                "DENIED"
                        );
                    }

                    if (isSuccess) {
                        // In a real implementation, we would update the booking status here
                        return new PaymentResponseDto(
                                true,
                                transactionId,
                                "Payment processed successfully",
                                LocalDateTime.now(),
                                paymentRequest.getBookingId(),
                                paymentRequest.getAmount(),
                                "COMPLETED"
                        );
                    } else {
                        return new PaymentResponseDto(
                                false,
                                "",
                                "Payment declined by bank",
                                LocalDateTime.now(),
                                paymentRequest.getBookingId(),
                                paymentRequest.getAmount(),
                                "DECLINED"
                        );
                    }
                })
                .orElse(new PaymentResponseDto(
                        false,
                        "",
                        "Booking not found",
                        LocalDateTime.now(),
                        paymentRequest.getBookingId(),
                        paymentRequest.getAmount(),
                        "ERROR"
                ));
    }

    @Override
    public PaymentResponseDto getPaymentStatus(Long bookingId, String userEmail) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    if (!booking.getUser().getEmail().equals(userEmail)) {
                        return new PaymentResponseDto(
                                false,
                                "",
                                "Unauthorized access to booking",
                                LocalDateTime.now(),
                                bookingId,
                                null,
                                "DENIED"
                        );
                    }

                    // In a real implementation, we would check the actual payment status
                    // For the mock, we'll randomly return a status
                    String[] statuses = {"PENDING", "COMPLETED", "REFUNDED", "FAILED"};
                    String status = statuses[random.nextInt(statuses.length)];
                    
                    return new PaymentResponseDto(
                            status.equals("COMPLETED") || status.equals("REFUNDED"),
                            "TXN" + UUID.randomUUID().toString().substring(0, 12).toUpperCase(),
                            "Payment status: " + status,
                            LocalDateTime.now(),
                            bookingId,
                            booking.getPrice(),
                            status
                    );
                })
                .orElse(new PaymentResponseDto(
                        false,
                        "",
                        "Booking not found",
                        LocalDateTime.now(),
                        bookingId,
                        null,
                        "ERROR"
                ));
    }
}
