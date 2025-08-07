package com.epam.campstone.eventbookingsystem.dto;

import com.epam.campstone.eventbookingsystem.model.Booking;
import com.epam.campstone.eventbookingsystem.model.BookingStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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

    @NotNull(message = "Seats are required")
    private List<SeatDto> seats;
}