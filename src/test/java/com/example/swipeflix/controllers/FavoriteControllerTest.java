package com.example.swipeflix.controllers;

import com.example.swipeflix.models.Movie;
import com.example.swipeflix.payload.response.MessageResponse;
import com.example.swipeflix.services.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FavoriteControllerTest {

    @Mock
    FavoriteService favoriteService;

    @InjectMocks
    FavoriteController favoriteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addMovie_ShouldReturnOkResponse_WhenSuccessfullyAdded() {
        Long userId = 1L;
        Long movieId = 2L;
        MessageResponse messageResponse = new MessageResponse("Movie successfully added to favorites!");
        when(favoriteService.addFavorite(userId, movieId)).thenReturn(messageResponse);

        ResponseEntity<MessageResponse> result = favoriteController.addFavorite(userId, movieId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(messageResponse, result.getBody());
    }

    @Test
    void addMovie_ShouldReturnBadRequest_WhenAddFails() {
        Long userId = 1L;
        Long movieId = 2L;
        MessageResponse messageResponse = new MessageResponse("Error adding movie to favorites.");
        when(favoriteService.addFavorite(userId, movieId)).thenReturn(messageResponse);

        ResponseEntity<MessageResponse> result = favoriteController.addFavorite(userId, movieId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(messageResponse, result.getBody());

    }

    @Test
    void removeMovie_ShouldReturnOkResponse_WhenSuccessfullyRemoved() {
        Long userId = 1L;
        Long movieId = 2L;
        MessageResponse messageResponse = new MessageResponse("Successfully removed from favorites!");
        when(favoriteService.removeFavorite(userId, movieId)).thenReturn(messageResponse);

        ResponseEntity<MessageResponse> result = favoriteController.removeFavorite(userId, movieId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(messageResponse, result.getBody());


    }

    @Test
    void removeFavorite_ShouldReturnBadRequestResponse_WhenRemoveFails() {
        Long userId = 1L;
        Long movieId = 2L;
        MessageResponse response = new MessageResponse("Error removing movie from favorites.");
        when(favoriteService.removeFavorite(userId, movieId)).thenReturn(response);

        ResponseEntity<MessageResponse> result = favoriteController.removeFavorite(userId, movieId);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(response, result.getBody());
    }


    @Test
    void getFavoriteMovies_ShouldReturnOkResponse_WithFavoriteMovieList() {
        Long userId = 1L;
        Set<Movie> favoriteMovies = new HashSet<>();
        favoriteMovies.add(new Movie()); //write an example movie, or not
        when(favoriteService.getUserFavorites(userId)).thenReturn(favoriteMovies);

        ResponseEntity<?> result = favoriteController.getFavoriteMovies(userId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(favoriteMovies, result.getBody());
    }

    @Test
    void getFavoriteMovies_ShouldReturnEmptyList_WhenNoFavoritesFound() {
        Long userId = 1L;
        Set<Movie> emptyFavorites = Collections.emptySet();
        when(favoriteService.getUserFavorites(userId)).thenReturn(emptyFavorites);

        ResponseEntity<?> result = favoriteController.getFavoriteMovies(userId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(emptyFavorites, result.getBody());
    }

}