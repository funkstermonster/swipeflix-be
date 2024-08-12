package com.example.swipeflix.controllers;

import com.example.swipeflix.models.Movie;
import com.example.swipeflix.models.PosterBlob;
import com.example.swipeflix.payload.request.PatchMovieDto;
import com.example.swipeflix.repository.MovieRepository;
import com.example.swipeflix.services.MovieService;
import com.example.swipeflix.services.PosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private PosterService posterService;

    @PutMapping
    public ResponseEntity<?> saveMovie() {
        movieService.readCSVFile();
        return ResponseEntity.ok("Movies saved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        return movieService.findMovieById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/random")
    public ResponseEntity<List<Movie>> getRandomMovie() {
        List<Movie> randomMovie = movieService.findRandomMovies(10);
        return ResponseEntity.ok(randomMovie);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchMovie(@PathVariable Long id, @RequestBody PatchMovieDto patchMovieDto) {
        try {
            Movie updatedMovie = movieService.updateMoviePoster(id, patchMovieDto);
            return new ResponseEntity<>(updatedMovie, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

