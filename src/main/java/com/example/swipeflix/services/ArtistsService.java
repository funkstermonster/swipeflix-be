package com.example.swipeflix.services;

import com.example.swipeflix.models.Actor;
import com.example.swipeflix.models.Artist;
import com.example.swipeflix.models.Movie;
import com.example.swipeflix.repository.ArtistsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ArtistsService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ArtistsRepository artistsRepository;
    @Autowired
    private MovieService movieService;
    private List<Movie> movies = new ArrayList<>();

    public void readCreditsCSVFile() {
        String csvFilePath = "/credits.csv";

        try (InputStream inputStream = ArtistsService.class.getResourceAsStream(csvFilePath);
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            List<String[]> records = reader.readAll();

            if (records.isEmpty()) {
                System.err.println("No records found in the CSV file.");
                return;
            }

            boolean isFirstRow = true;
            for (String[] record : records) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                if (record.length < 3) {
                    System.err.println("Skipping record due to insufficient columns: " + String.join(",", record));
                    continue;
                }

                String jsonString = record[0];
                Long movieId = Long.parseLong(record[2]);
                Optional<Movie> optionalMovie = movieService.findMovieById(movieId);
                if (jsonString == null || jsonString.trim().isEmpty()) {
                    System.err.println("Empty JSON string for record: " + String.join(",", record));
                    continue;
                }

                try {
                    String correctedJsonString = correctJsonString(jsonString);
                    List<Actor> actors = parseActors(correctedJsonString);
                    if (optionalMovie.isPresent() && !actors.isEmpty()) {
                        Movie movie = optionalMovie.get();
                        Set<Artist> artistSet = new HashSet<>();

                        for (Actor actor : actors) {
                            Optional<Artist> existingArtistOpt = artistsRepository.findByName(actor.getActor_name());
                            Artist artist = existingArtistOpt.orElseGet(() -> {
                                Artist newArtist = new Artist();
                                newArtist.setName(actor.getActor_name());
                                return newArtist;
                            });

                            artistSet.add(artist);
                        }

                        // Save the artists in batch
                        List<Artist> savedArtists = artistsRepository.saveAll(new ArrayList<>(artistSet));
                        movie.setArtists(new HashSet<>(savedArtists));
                        movies.add(movie);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to process record: " + String.join(",", record));
                    e.printStackTrace();
                }
            }
            movieService.saveEntities(movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Actor> parseActors(String jsonString) throws Exception {
        List<Actor> actors = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        if (rootNode.isArray() && !rootNode.isEmpty()) {
            int count = 0;
            for (JsonNode node : rootNode) {
                if (count >= 4) break;
                String character = node.path("character").asText();
                String name = node.path("name").asText();
                Actor actor = new Actor();
                actor.setCharacter_name(character);
                actor.setActor_name(name);
                actors.add(actor);
                count++;
            }
        }
        return actors;
    }

    private String correctJsonString(String jsonString) {
        // 1. Replace single quotes around field names with double quotes
        jsonString = jsonString.replaceAll("'([a-zA-Z_]+)'(?=\\s*:)", "\"$1\"");

        // 2. Replace single quotes around values with double quotes
        // and escape existing double quotes
        Pattern valuePattern = Pattern.compile(":(\\s*)'([^']*)'");
        Matcher valueMatcher = valuePattern.matcher(jsonString);
        StringBuffer sb = new StringBuffer();

        while (valueMatcher.find()) {
            String value = valueMatcher.group(2);
            // Escape double quotes inside the value
            String escapedValue = value.replace("\"", "\\\"");
            valueMatcher.appendReplacement(sb, ": \"" + escapedValue + "\"");
        }
        valueMatcher.appendTail(sb);
        jsonString = sb.toString();

        // 3. Replace None with null
        jsonString = jsonString.replaceAll("\\bNone\\b", "null");

        // 4. Remove trailing commas before closing brackets
        jsonString = jsonString.replaceAll(",\\s*(\\}|\\])", "$1");

        // 5. Ensure the string is a valid JSON array
        if (!jsonString.startsWith("[")) {
            jsonString = "[" + jsonString;
        }
        if (!jsonString.endsWith("]")) {
            jsonString = jsonString + "]";
        }

        return jsonString;
    }
}
