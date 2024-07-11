package com.example.swipeflix.services;

import com.example.swipeflix.models.Genre;
import com.example.swipeflix.models.Movie;
import com.example.swipeflix.models.User;
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
        updateUserPreferences(userId, movie.getGenres(), true);
    }

    public void handleSwipeLeft(Long userId, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow();
        updateUserPreferences(userId, movie.getGenres(), false);
    }

    private void updateUserPreferences(Long userId, Set<Genre> genres, boolean isLiked) {

        UserPreferences userPreferences = userPreferencesService.findUserPreferences(userId);

        if (isLiked) {
            userPreferences.addLikedGenres(genres);
        } else {
            userPreferences.addDislikedGenres(genres);
        }

        userPreferencesService.saveUserPreferences(userPreferences);

    }

    @Transactional(readOnly = true)
    public Movie recommendedMovieFromSameGenre(Long userId) {
        UserPreferences userPreferences = userPreferencesMap.get(userId);
        if (userPreferences == null) {
            return null;
        }

        List<Movie> movies = movieRepository.findAll();
        Set<Genre> likedEGenres = userPreferences.getLikedEGenres();

        List<Movie> sameGenreMovies = movies.stream()
                .filter(movie -> !Collections.disjoint(movie.getGenres(), likedEGenres))
                .collect(Collectors.toList());

        return getRandomMovie(sameGenreMovies);
    }

    @Transactional(readOnly = true)
    public Movie recommendMovieFromDifferentGenre(Long userId) {
        UserPreferences userPreferences = userPreferencesMap.get(userId);

        if (userPreferences == null) {
            return null;
        }
        List<Movie> movies = movieRepository.findAll();
        Set<Genre> dislikedEGenres = userPreferences.getDislikedEGenres();

        List<Movie> differentGenreMovies = movies.stream()
                .filter(movie -> Collections.disjoint(movie.getGenres(), dislikedEGenres))
                .collect(Collectors.toList());

        return getRandomMovie(differentGenreMovies);
    }

    private Movie getRandomMovie(List<Movie> movies) {
        if (movies.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * movies.size());
        return movies.get(randomIndex);
    }
}
