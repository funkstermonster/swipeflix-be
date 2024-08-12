package com.example.swipeflix.controllers;

import com.example.swipeflix.models.Movie;
import com.example.swipeflix.services.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Movie>> getRecommendation(@PathVariable Long userId) {
        return new ResponseEntity<>(recommendationService.recommendMovie(userId), HttpStatus.OK);
    }
}
