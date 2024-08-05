package com.example.swipeflix.services;

import com.example.swipeflix.models.Genre;
import com.example.swipeflix.models.Movie;
import com.example.swipeflix.models.UserPreferences;
import com.example.swipeflix.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private UserPreferencesService userPreferencesService;

    @Autowired
    private MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<Movie> recommendMovie(Long userId) {
        UserPreferences userPreferences = userPreferencesService.findUserPreferences(userId);
        if (userPreferences == null) {
            return Collections.emptyList();
        }

        List<Movie> allMovies = movieRepository.findAll();
        Set<Genre> likedGenres = userPreferences.getLikedGenres() != null ? userPreferences.getLikedGenres() : Collections.emptySet();
        Set<Genre> dislikedGenres = userPreferences.getDislikedGenres() != null ? userPreferences.getDislikedGenres() : Collections.emptySet();
        Set<Movie> likedMovies = userPreferences.getLikedMovies() != null ? userPreferences.getLikedMovies() : Collections.emptySet();
        Set<Movie> dislikedMovies = userPreferences.getDislikedMovies() != null ? userPreferences.getDislikedMovies() : Collections.emptySet();

        Map<Genre, Long> genreFrequency = calculateGenreFrequency(likedMovies);

        List<Movie> recommendedMovies = allMovies.stream()
                .filter(movie -> !likedMovies.contains(movie))
                .filter(movie -> !dislikedMovies.contains(movie))
                .filter(movie -> !Collections.disjoint(movie.getGenres(), likedGenres))
                .filter(movie -> Collections.disjoint(movie.getGenres(), dislikedGenres))
                .sorted((m1, m2) -> {
                    long score1 = calculateGenreMatchScore(m1, genreFrequency);
                    long score2 = calculateGenreMatchScore(m2, genreFrequency);
                    return Long.compare(score2, score1);
                })
                .collect(Collectors.toList());

        // Fallback to default recommendations if the list is empty
        if (recommendedMovies.isEmpty()) {
            recommendedMovies = getDefaultRecommendations(allMovies, likedMovies, dislikedMovies);
        }

        return getTopMovies(recommendedMovies);
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

    private List<Movie> getTopMovies(List<Movie> movies) {
        return movies.stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<Movie> getDefaultRecommendations(List<Movie> allMovies, Set<Movie> likedMovies, Set<Movie> dislikedMovies) {
        return allMovies.stream()
                .filter(movie -> !likedMovies.contains(movie))
                .filter(movie -> !dislikedMovies.contains(movie))
                .limit(5) // Arbitrary limit for default recommendations
                .collect(Collectors.toList());
    }
}
