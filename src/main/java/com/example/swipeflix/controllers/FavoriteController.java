package com.example.swipeflix.controllers;

import com.example.swipeflix.payload.response.MessageResponse;
import com.example.swipeflix.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addFavorite(@RequestParam Long userId, @RequestParam Long movieId) {
        MessageResponse result = favoriteService.addFavorite(userId, movieId);
        if (result.getMessage().equals("Movie successfully added to favorites!")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<MessageResponse> removeFavorite(@RequestParam Long userId, @RequestParam Long movieId) {
        MessageResponse result = favoriteService.removeFavorite(userId, movieId);
        if (result.getMessage().equals("Successfully removed from favorites!")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFavoriteMovies(@PathVariable Long userId) {
       return new ResponseEntity<>(favoriteService.getUserFavorites(userId), HttpStatus.OK) ;
    }
}
