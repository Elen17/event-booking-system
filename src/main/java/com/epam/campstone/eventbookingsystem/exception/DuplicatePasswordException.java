package com.epam.campstone.eventbookingsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatePasswordException extends RuntimeException {
    public DuplicatePasswordException(String message) {
        super(message);
    }
}
