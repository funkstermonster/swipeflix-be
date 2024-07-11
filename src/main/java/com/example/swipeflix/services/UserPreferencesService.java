package com.example.swipeflix.services;

import com.example.swipeflix.models.User;
import com.example.swipeflix.models.UserPreferences;
import com.example.swipeflix.repository.UserPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPreferencesService {

    @Autowired
    UserPreferencesRepository userPreferencesRepository;

    @Autowired
    UserService userService;


    public UserPreferences findUserPreferences(Long userId) {
        User user = userService.findById(userId).get();
        return user.getUserPreferences();
    }

    public UserPreferences saveUserPreferences(UserPreferences userPreferences) {
        return userPreferencesRepository.save(userPreferences);
    }
}
