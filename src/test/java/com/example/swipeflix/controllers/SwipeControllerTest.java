package com.example.swipeflix.controllers;

import com.example.swipeflix.payload.response.MessageResponse;
import com.example.swipeflix.services.SwipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class SwipeControllerTest {

    @Mock
    SwipeService swipeService;

    @InjectMocks
    SwipeController swipeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void swipeRight_ShouldReturnSuccessMessage_WhenSuccessful() {
        Long userId = 1L;
        Long movieId = 100L;

        ResponseEntity<MessageResponse> response = swipeController.swipeRight(userId, movieId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Movie successfully swiped right.", response.getBody().getMessage());
        verify(swipeService).handleSwipeRight(userId, movieId);
    }

    @Test
    void swipeRight_ShouldReturnErrorMessage_WhenExceptionThrown() {
        Long userId = 1L;
        Long movieId = 100L;
        doThrow(new RuntimeException("Service failure")).when(swipeService).handleSwipeRight(userId, movieId);

        ResponseEntity<MessageResponse> response = swipeController.swipeRight(userId, movieId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while processing your swipe right request: Service failure", response.getBody().getMessage());
        verify(swipeService).handleSwipeRight(userId, movieId);
    }


    @Test
    void swipeLeft_ShouldReturnSuccessMessage_WhenSuccessful() {
        Long userId = 1L;
        Long movieId = 100L;

        ResponseEntity<MessageResponse> response = swipeController.swipeLeft(userId, movieId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Movie successfully swiped left.", response.getBody().getMessage());
        verify(swipeService).handleSwipeLeft(userId, movieId);
    }

    @Test
    void swipeLeft_ShouldReturnErrorMessage_WhenExceptionThrown() {
        Long userId = 1L;
        Long movieId = 100L;
        doThrow(new RuntimeException("Service failure")).when(swipeService).handleSwipeLeft(userId, movieId);

        ResponseEntity<MessageResponse> response = swipeController.swipeLeft(userId, movieId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while processing your swipe left request: Service failure", response.getBody().getMessage());
        verify(swipeService).handleSwipeLeft(userId, movieId);
    }
}