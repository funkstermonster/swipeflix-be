package com.example.swipeflix.controllers;

import com.example.swipeflix.models.Movie;
import com.example.swipeflix.repository.MovieRepository;
import com.example.swipeflix.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @PutMapping()
    public ResponseEntity<?> saveMovie() {
        try {
            movieService.readCSVFile();
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        try {
            Movie movie = movieRepository.findById(id).orElse(null);
            if (movie == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}

