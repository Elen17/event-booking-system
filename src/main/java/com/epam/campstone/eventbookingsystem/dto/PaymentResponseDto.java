package com.epam.campstone.eventbookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private boolean success;
    private String transactionId;
    private String message;
    private LocalDateTime timestamp;
    private Long bookingId;
    private BigDecimal amount;
    private String status;
}
