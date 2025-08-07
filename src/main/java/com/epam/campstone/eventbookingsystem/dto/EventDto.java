package com.epam.campstone.eventbookingsystem.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDto {
    private Long id;

    @NotBlank(message = "Event title is required")
    @Size(max = 100, message = "Event title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotBlank(message = "Venue is required")
    @Size(max = 200, message = "Venue must be less than 200 characters")
    private VenueDto venue;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer attendeesCapacity;

    @NotNull(message = "Available capacity is required")
    @Min(value = 1, message = "Available capacity must be at least 1")
    private Integer availableAttendeesCapacity;


}
