package com.example.swipeflix.services;

import com.example.swipeflix.models.Genre;
import com.example.swipeflix.models.Movie;
import com.example.swipeflix.models.UserPreferences;
import com.example.swipeflix.repository.GenreRepository;
import com.example.swipeflix.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

