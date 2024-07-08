package com.example.swipeflix.services;

import com.example.swipeflix.models.Genre;
import com.example.swipeflix.models.Movie;
import com.example.swipeflix.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovieService {


    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ObjectMapper objectMapper;


    public void readCSVFile() {
        String csvFilePath = "/movies_metadata.csv";
        try (InputStream inputStream = MovieService.class.getResourceAsStream(csvFilePath);
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            boolean isFirstRow = true;
            List<String[]> records = reader.readAll();

            List<Movie> movies = new ArrayList<>();

            for (String[] record : records) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                if (record.length < 21) {
                    continue;
                }
                String title = record[20];
                String overview = record[9];
                String releaseDateString = record[14].trim(); // Trim to remove any leading/trailing whitespace
                if (releaseDateString.isEmpty()) {
                    continue;
                }
                LocalDate releaseDate = parseReleaseDate(releaseDateString);
                String genresJson = record[3];
                List<String> genres = extractGenres(genresJson);
                String posterPath = record[11];
                Integer runtime = parseRuntime(record[16]);
                Double rating = parseRating(record[22]);
                Long id = Long.valueOf(record[5]);
                String imdbId = record[6];
                // Process each record as needed
                System.out.println("id: " + id);

                Movie movie = Movie.builder().id(id).imdbId(imdbId).title(title).overview(overview).releaseDate(releaseDate).genres(genres).posterPath(posterPath).runtime(runtime).rating(rating).build();
                movies.add(movie);
            }
            saveEntities(movies);

            System.out.println("CSV file has been parsed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Async("taskExecutor")
    public void saveEntities(List<Movie> movies) {
        movieRepository.saveAll(movies);
    }

    private Double parseRating(String ratingString) {
        if (ratingString == null || ratingString.trim().isEmpty() || ratingString.equals("vote_average")) {
            return 0.0;
        }
        try {
            return Double.parseDouble(ratingString.trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid rating format: " + ratingString);
            return 0.0;
        }
    }

    private Integer parseRuntime(String runtimeString) {
        if (runtimeString == null || runtimeString.trim().isEmpty() || runtimeString.equals("runtime")) {
            return 0;
        }
        try {
            Double runtimeDouble = Double.parseDouble(runtimeString);
            return runtimeDouble.intValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }


    private LocalDate parseReleaseDate(String dateString) throws DateTimeParseException {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            if (dateString.equals("release_date")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private List<String> extractGenres(String input) {
        Pattern pattern = Pattern.compile("'name': '([A-Za-z ]+)'");
        Matcher matcher = pattern.matcher(input);
        Set<String> validGenres = Arrays.stream(Genre.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        return matcher.results()
                .map(match -> match.group(1).toUpperCase().replace(" ", "_"))
                .map(genre -> {
                    if (validGenres.contains(genre)) {
                        return genre.toLowerCase().replace("_", " ");
                    } else {
                        return "UNKNOWN";
                    }
                })
                .map(genre -> genre.substring(0, 1).toUpperCase() + genre.substring(1).toLowerCase())
                .collect(Collectors.toList());
    }
}
