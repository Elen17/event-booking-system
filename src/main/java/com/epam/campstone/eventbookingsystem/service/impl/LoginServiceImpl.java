package com.epam.campstone.eventbookingsystem.service.impl;

import com.epam.campstone.eventbookingsystem.service.api.LoginService;
import com.epam.campstone.eventbookingsystem.service.api.UserService;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    private UserService userService;

    public LoginServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean login(String username, String password) {

    }

    @Override
    public boolean logout() {
    }
}
