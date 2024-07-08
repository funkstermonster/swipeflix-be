package com.example.swipeflix.services;

import com.example.swipeflix.models.Movie;
import com.example.swipeflix.repository.MovieRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OkHttpService {

    private final String API_KEY = "29b85729";
    private final String BASE_URL = "http://www.omdbapi.com/";

    @Autowired
    private MovieRepository movieRepository;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Movie fetchAndSaveMovie(String title) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "?t=" + title + "&apikey=" + API_KEY)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);

            String jsonData = response.body().string();
            Movie movie = objectMapper.readValue(jsonData, Movie.class);

            return movieRepository.save(movie);
        }
    }
}
