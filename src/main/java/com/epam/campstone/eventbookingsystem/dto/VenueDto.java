package com.epam.campstone.eventbookingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueDto {
    private Long id;

    @NotBlank(message = "Venue name is required")
    @Size(max = 255, message = "Venue name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must be less than 500 characters")
    private String address;

    @NotNull(message = "City ID is required")
    private Integer cityId;
}