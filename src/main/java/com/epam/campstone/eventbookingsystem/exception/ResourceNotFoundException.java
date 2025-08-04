package com.epam.campstone.eventbookingsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the root cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Helper method to create a ResourceNotFoundException with a formatted message.
     *
     * @param resourceName the name of the resource (e.g., "User", "Event")
     * @param fieldName the name of the field being searched
     * @param fieldValue the value of the field that wasn't found
     * @return a new ResourceNotFoundException with a formatted message
     */
    public static ResourceNotFoundException forResource(String resourceName, String fieldName, Object fieldValue) {
        String message = String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue);
        return new ResourceNotFoundException(message);
    }
}
