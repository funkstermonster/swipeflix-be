package com.example.swipeflix.controllers;

import com.example.swipeflix.services.ArtistsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/artists")
public class ArtistsController {

    @Autowired
    ArtistsService artistsService;

    @PutMapping
    public ResponseEntity<?> saveArtists() {
        try {
            artistsService.readCreditsCSVFile();
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
