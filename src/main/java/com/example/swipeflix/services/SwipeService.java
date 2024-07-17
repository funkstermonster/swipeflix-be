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

//    private void updateUserPreferences(Long userId, Set<Genre> genres, boolean isLiked) {
//
//        UserPreferences userPreferences = userPreferencesService.findUserPreferences(userId);
//
//        if (isLiked) {
//            userPreferences.addLikedGenres(genres);
//        } else {
//            userPreferences.addDislikedGenres(genres);
//        }
//
//        userPreferencesService.saveUserPreferences(userPreferences);
//
//    }

    @Transactional(readOnly = true)
    public Movie recommendMovie(Long userId) {
        UserPreferences userPreferences = userPreferencesService.findUserPreferences(userId);
        if (userPreferences == null) {
            return null;
        }

        List<Movie> allMovies = movieRepository.findAll();
        Set<Genre> likedGenres = userPreferences.getLikedGenres();
        Set<Genre> dislikedGenres = userPreferences.getDislikedGenres();
        Map<Genre, Long> genreFrequency = calculateGenreFrequency(userPreferences.getLikedMovies());

        List<Movie> recommendedMovies = allMovies.stream()
                .filter(movie -> !userPreferences.getLikedMovies().contains(movie))
                .filter(movie -> !userPreferences.getDislikedMovies().contains(movie))
                .filter(movie -> !Collections.disjoint(movie.getGenres(), likedGenres))
                .filter(movie -> Collections.disjoint(movie.getGenres(), dislikedGenres))
                .sorted((m1, m2) -> {
                    long score1 = calculateGenreMatchScore(m1, genreFrequency);
                    long score2 = calculateGenreMatchScore(m2, genreFrequency);
                    return Long.compare(score2, score1);
                })
                .collect(Collectors.toList());

        return getRandomMovie(recommendedMovies);
    }

    private Map<Genre, Long> calculateGenreFrequency(Set<Movie> likedMovies) {
        return likedMovies.stream()
                .flatMap(movie -> movie.getGenres().stream())
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()));
    }

    private long calculateGenreMatchScore(Movie movie, Map<Genre, Long> genreFrequency) {
        return movie.getGenres().stream()
                .mapToLong(genre -> genreFrequency.getOrDefault(genre, 0L))
                .sum();
    }

    private Movie getRandomMovie(List<Movie> movies) {
        if (movies.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * movies.size());
        return movies.get(randomIndex);
    }
}

