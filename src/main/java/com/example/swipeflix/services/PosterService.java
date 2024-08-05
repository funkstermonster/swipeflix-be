package com.example.swipeflix.services;

import com.example.swipeflix.models.PosterBlob;
import com.example.swipeflix.repository.PosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PosterService {

    @Autowired
    private PosterRepository posterRepository;

    public void savePoster(PosterBlob poster) {
        posterRepository.save(poster);
    }
}
