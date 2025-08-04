package com.epam.campstone.eventbookingsystem.service.api;

import com.epam.campstone.eventbookingsystem.model.User;

public interface UserService {

    void saveUser(User user);

    User getUser(String username);

    void updateUser(User user);
}
