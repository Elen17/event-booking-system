package com.epam.campstone.eventbookingsystem.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for token refresh request
 */
public class TokenRefreshRequest {
    @NotBlank
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
