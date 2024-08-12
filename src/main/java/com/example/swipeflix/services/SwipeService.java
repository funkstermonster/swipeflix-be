package com.example.swipeflix.services;

import com.example.swipeflix.models.Movie;
import com.example.swipeflix.models.UserPreferences;
import com.example.swipeflix.repository.GenreRepository;
import com.example.swipeflix.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SwipeService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private UserPreferencesService userPreferencesService;

    private final Map<Long, UserPreferences> userPreferencesMap = new ConcurrentHashMap<>();

    public void handleSwipeRight(Long userId, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        UserPreferences userPreferences = userPreferencesService.findUserPreferences(userId);

        if (userPreferences.getDislikedMovies().contains(movie)) {
            userPreferences.removeDislikedMovie(movie);
        }

        if (!userPreferences.getLikedMovies().contains(movie)) {
            userPreferences.addLikedMovie(movie);
        }

        userPreferencesService.saveUserPreferences(userPreferences);
    }

    public void handleSwipeLeft(Long userId, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        UserPreferences userPreferences = userPreferencesService.findUserPreferences(userId);

        if (userPreferences.getLikedMovies().contains(movie)) {
            userPreferences.removeLikedMovie(movie);
        }

        if (!userPreferences.getDislikedMovies().contains(movie)) {
            userPreferences.addDislikedMovie(movie);
        }

        userPreferencesService.saveUserPreferences(userPreferences);
    }
}

