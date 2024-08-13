package com.example.swipeflix.controllers;

import com.example.swipeflix.models.Movie;
import com.example.swipeflix.services.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RecommendationControllerTest {

    @Mock
    RecommendationService recommendationService;

    @InjectMocks
    RecommendationController recommendationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getRecommendation_ShouldReturnMovies_WhenUserHasRecommendations() {
        Movie movie1 = Movie.builder().id(1L).title("Inception").build();
        Movie movie2 = Movie.builder().id(2L).title("Interstellar").build();
        List<Movie> recommendedMovies = Arrays.asList(movie1, movie2);

        when(recommendationService.recommendMovie(1L)).thenReturn(recommendedMovies);

        ResponseEntity<List<Movie>> response = recommendationController.getRecommendation(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recommendedMovies, response.getBody());
    }

    @Test
    void getRecommendation_ShouldReturnOkResponse_WithEmptyList_WhenNoRecommendationsFound() {
        Long userId = 1L;
        when(recommendationService.recommendMovie(userId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Movie>> response = recommendationController.getRecommendation(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void getRecommendation_ShouldReturnEmptyList_WhenUserPreferencesNotFound() {
        when(recommendationService.recommendMovie(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Movie>> response = recommendationController.getRecommendation(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void getRecommendation_ShouldHandleServiceExceptionGracefully() {
        when(recommendationService.recommendMovie(1L)).thenThrow(new RuntimeException("Service failure"));

        ResponseEntity<List<Movie>> response = recommendationController.getRecommendation(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }
}