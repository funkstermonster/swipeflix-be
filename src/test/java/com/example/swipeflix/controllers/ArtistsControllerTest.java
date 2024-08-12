package com.example.swipeflix.controllers;

import com.example.swipeflix.services.ArtistsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class ArtistsControllerTest {

    @Mock
    private ArtistsService artistsService;

    @InjectMocks
    private ArtistsController artistsController;

    @BeforeEach
    void setUp()  {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveArtists_shouldReturnOkResponse() {
        doNothing().when(artistsService).readCreditsCSVFile();

        ResponseEntity<?> response = artistsController.saveArtists();

        assertEquals("Artists saved successfully!", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(artistsService).readCreditsCSVFile();

    }
}