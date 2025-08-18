package com.epam.campstone.eventbookingsystem.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResponseStatusException(ResponseStatusException ex, Model model) {
        log.error("Handling ResponseStatusException: {}", ex.getMessage());
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            model.addAttribute("errorMessage", ex.getReason());
            return "error/not-found";
        }
        throw ex;
    }
}
