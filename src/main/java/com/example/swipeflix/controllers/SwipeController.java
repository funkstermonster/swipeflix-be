package com.example.swipeflix.controllers;


import com.example.swipeflix.models.Movie;
import com.example.swipeflix.services.MovieService;
import com.example.swipeflix.services.SwipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/swipe")
public class SwipeController {

    @Autowired
    SwipeService swipeService;

    @PostMapping("/right/{userId}/{movieId}")
    public ResponseEntity<?> swipeRight(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            swipeService.handleSwipeRight(userId, movieId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/left/{userId}/{movieId}")
    public ResponseEntity<?> swipeLeft(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            swipeService.handleSwipeLeft(userId, movieId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
