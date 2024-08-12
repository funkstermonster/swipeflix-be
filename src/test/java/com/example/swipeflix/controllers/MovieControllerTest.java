package com.example.swipeflix.controllers;

import com.example.swipeflix.models.Movie;
import com.example.swipeflix.payload.request.PatchMovieDto;
import com.example.swipeflix.services.MovieService;
import com.example.swipeflix.services.PosterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @Mock
    private PosterService posterService;

    @InjectMocks
    private MovieController movieController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveMovie_ShouldReturnOkResponse() {
        doNothing().when(movieService).readCSVFile();
        ResponseEntity<?> response = movieController.saveMovie();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Movies saved successfully", response.getBody());
        verify(movieService, times(1)).readCSVFile();
    }

    @Test
    void getMovieById_ShouldReturnMovie_WhenMovieExists() {
        Long movieId = 1L;
        Movie movie = new Movie();
        when(movieService.findMovieById(movieId)).thenReturn(Optional.of(movie));

        ResponseEntity<?> response = movieController.getMovieById(movieId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movie, response.getBody());
        verify(movieService, times(1)).findMovieById(movieId);
    }

    @Test
    void getMovieById_ShouldReturnNotFound_WhenMovieDoesNotExist() {
        Long movieId = 1L;
        when(movieService.findMovieById(movieId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = movieController.getMovieById(movieId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(movieService, times(1)).findMovieById(movieId);
    }


    @Test
    void getRandomMovie_ShouldReturnListOfMovies() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie());
        when(movieService.findRandomMovies(10)).thenReturn(movies);

        ResponseEntity<List<Movie>> response = movieController.getRandomMovie();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movies, response.getBody());

        verify(movieService, times(1)).findRandomMovies(10);
    }

    @Test
    void patchMovie_ShouldReturnOkResponse_WhenUpdateIsSuccessful() {

        Movie movie = new Movie();
        movie.setId(1L);
        when(movieService.updateMoviePoster(anyLong(), any(PatchMovieDto.class))).thenReturn(movie);

        PatchMovieDto patchMovieDto = new PatchMovieDto();

        ResponseEntity<?> response = movieController.patchMovie(1L, patchMovieDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movie, response.getBody());
    }

    @Test
    void patchMovie_ShouldReturnNotFound_WhenMovieIsNotFound() {
        when(movieService.updateMoviePoster(anyLong(), any(PatchMovieDto.class)))
                .thenThrow(new NoSuchElementException("Movie not found"));

        PatchMovieDto patchMovieDto = new PatchMovieDto();

        ResponseEntity<?> response = movieController.patchMovie(1L, patchMovieDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Movie not found", response.getBody());
    }

    @Test
    void patchMovie_ShouldReturnInternalServerError_WhenUnexpectedErrorOccurs() {
        when(movieService.updateMoviePoster(anyLong(), any(PatchMovieDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        PatchMovieDto patchMovieDto = new PatchMovieDto();

        ResponseEntity<?> response = movieController.patchMovie(1L, patchMovieDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody());
    }

}