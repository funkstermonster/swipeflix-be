package com.example.swipeflix.controllers;


import com.example.swipeflix.payload.response.MessageResponse;
import com.example.swipeflix.services.SwipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<MessageResponse> swipeRight(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            swipeService.handleSwipeRight(userId, movieId);
            return ResponseEntity.ok(new MessageResponse("Movie successfully swiped right."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An error occurred while processing your swipe right request: " + e.getMessage()));
        }
    }

    @PostMapping("/left/{userId}/{movieId}")
    public ResponseEntity<MessageResponse> swipeLeft(@PathVariable Long userId, @PathVariable Long movieId) {
        try {
            swipeService.handleSwipeLeft(userId, movieId);
            return ResponseEntity.ok(new MessageResponse("Movie successfully swiped left."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An error occurred while processing your swipe left request: " + e.getMessage()));
        }
    }
}
