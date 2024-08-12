package com.example.swipeflix.services;

import com.example.swipeflix.models.EGenre;
import com.example.swipeflix.models.Genre;
import com.example.swipeflix.models.Movie;
import com.example.swipeflix.models.PosterBlob;
import com.example.swipeflix.payload.request.PatchMovieDto;
import com.example.swipeflix.repository.GenreRepository;
import com.example.swipeflix.repository.MovieRepository;
import com.example.swipeflix.repository.PosterRepository;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MovieService {


    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PosterService posterService;


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
                Set<Genre> genres = extractGenres(genresJson);
                String posterPath = record[11];
                Integer runtime = parseRuntime(record[16]);
                Double rating = parseRating(record[22]);
                Long id = Long.valueOf(record[5]);
                String imdbId = record[6];
                String originalTitle = record[8];
                String originalLanguage = record[7];
                // Process each record as needed
                System.out.println("original title " + originalTitle);

                Movie movie = Movie.builder()
                        .id(id)
                        .imdbId(imdbId)
                        .title(title)
                        .overview(overview)
                        .releaseDate(releaseDate)
                        .genres(genres)
                        .posterPath(posterPath)
                        .runtime(runtime)
                        .rating(rating)
                        .originalTitle(originalTitle)
                        .originalLanguage(originalLanguage)
                        .build();
                if (movie.getOriginalLanguage().equals("en")) {
                    movies.add(movie);
                }
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

    @Transactional(readOnly = true)
    public Set<Genre> extractGenres(String input) {
        Pattern pattern = Pattern.compile("'name': '([A-Za-z ]+)'");
        Matcher matcher = pattern.matcher(input);
        Set<Genre> genres = new HashSet<>();

        while (matcher.find()) {
            String genreName = matcher.group(1).toUpperCase().replace(" ", "_");

            // Try to find the genre by actor_name in the database
            Optional<Genre> optionalGenre = genreRepository.findByName(EGenre.valueOf(genreName));
            optionalGenre.ifPresent(genres::add);
        }

        return genres;
    }

    public Optional<Movie> findMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

//    public boolean updateMoviePoster(Long id, String imgData) {
//        Optional<Movie> optionalMovie = findMovieById(id);
//        if (optionalMovie.isPresent()) {
//            Movie movie = optionalMovie.get();
//            PosterBlob posterBlob = new PosterBlob();
//            posterBlob.setImgData(imgData);
//            posterRepository.save(posterBlob);
//            movie.setPoster(posterBlob);
//            saveMovie(movie);
//            return true;
//        }
//        return false;
//    }

    @Transactional(readOnly = true)
    public List<Movie> findRandomMovies(int count) {
        List<Movie> allMovies = findAll();
        if (allMovies.isEmpty()) {
            throw new NoSuchElementException("No movies found");
        }
        if (allMovies.size() <= count) {
            return allMovies;
        }
        Collections.shuffle(allMovies);
        return allMovies.subList(0, count);
    }

    public Movie updateMoviePoster(Long id, PatchMovieDto patchMovieDto) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);

        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();
            PosterBlob posterBlob = new PosterBlob();
            posterBlob.setImgData(patchMovieDto.getImgData());
            posterService.savePoster(posterBlob);
            movie.setPoster(posterBlob);
            return movieRepository.save(movie);
        } else {
            throw new NoSuchElementException("Movie not found with id: " + id);
        }
    }
}
