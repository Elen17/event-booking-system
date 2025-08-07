package com.epam.campstone.eventbookingsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequestDto {
    @Min(value = 1, message = "Row number must be positive")
    private Integer rowNumber;

    @Min(value = 1, message = "Seat number must be positive")
    private Integer seatNumber;

    private String section;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal basePrice;

    private boolean isAvailable;
}