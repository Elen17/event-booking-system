package com.epam.campstone.eventbookingsystem.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when an authentication attempt is made with an inactive user account.
 */
public class UserNotActiveException extends AuthenticationException {

    /**
     * Constructs a new UserNotActiveException with the specified detail message.
     *
     * @param message the detail message
     */
    public UserNotActiveException(String message) {
        super(message);
    }
}
