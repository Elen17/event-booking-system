package com.epam.campstone.eventbookingsystem.dto;

import com.epam.campstone.eventbookingsystem.model.BookingStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookingDto {
    private Long id;

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Booking status is required")
    private BookingStatus bookingStatus;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Total amount is required")
    @Min(value = 1, message = "Total amount must be at least 1")
    private BigDecimal price;

    @NotNull(message = "Seats are required")
    private List<SeatDto> seats;
}
