package com.example.swipeflix.controllers;


import com.example.swipeflix.models.Movie;
import com.example.swipeflix.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/swipe")
public class SwipeController {

    @Autowired
    MovieService movieService;

    public ResponseEntity<?> swipeRight(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            Movie recommendedMovie = movieService.handleSwipeRight(userId, movieId);
            return ResponseEntity.ok(recommendedMovie);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    public ResponseEntity<?> swipeLeft(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            Movie recommendedMovie = movieService.handleSwipeLeft(userId, movieId);
            return ResponseEntity.ok(recommendedMovie);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
