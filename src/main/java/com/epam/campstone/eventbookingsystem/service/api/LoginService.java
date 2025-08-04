package com.epam.campstone.eventbookingsystem.service.api;

public interface LoginService {
    boolean login(String username, String password);
    boolean logout();
}
