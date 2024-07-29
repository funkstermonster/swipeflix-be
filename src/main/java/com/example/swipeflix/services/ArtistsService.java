package com.example.swipeflix.services;

import com.example.swipeflix.models.Actor;
import com.example.swipeflix.models.Artists;
import com.example.swipeflix.models.Movie;
import com.example.swipeflix.repository.ArtistsRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                Artists artists = new Artists();
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
                Movie movie;
                List<Actor> actors = new ArrayList<>();
                if (jsonString == null || jsonString.trim().isEmpty()) {
                    System.err.println("Empty JSON string for record: " + String.join(",", record));
                    continue;
                }
                String correctedJsonString = correctJsonString(jsonString);
                try {
                    JsonNode rootNode = objectMapper.readTree(correctedJsonString);
                    if (rootNode.isArray() && !rootNode.isEmpty()) {
                        int count = 0;
                        for (JsonNode node : rootNode) {
                            if (count >= 4) break;
                            if (optionalMovie.isEmpty()) break;
                            String character = node.path("character").asText();
                            String name = node.path("actor_name").asText();

                            System.out.printf("character: %s, actor_name: %s",
                                    character, name);
                            count++;

                            Actor actor = new Actor();
                            actor.setCharacter_name(character);
                            actor.setActor_name(name);
                            actors.add(actor);
                        }
                        if (optionalMovie.isPresent()) {
                            movie = optionalMovie.get();
                            artists.setActors(actors);
                            artistsRepository.save(artists);
                            movie.setArtists(artists);
                            movies.add(movie);
                        }
                    }
                } catch (JsonParseException e) {
                    System.err.println("Skipping unparsable line");
                    artists = new Artists();
                    break;
                } catch (Exception e) {
                    System.err.println("Failed to parse JSON string: " + correctedJsonString);
                    e.printStackTrace();
                    artists = new Artists();
                    break;
                }
            }
            movieService.saveEntities(movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String correctJsonString(String jsonString) {
        // 1. Replace single quotes around field names with double quotes
        jsonString = jsonString.replaceAll("'([a-zA-Z_]+)'(?=\\s*:)", "\"$1\"");

        // 2. Handle nested double quotes within string values
        // Replace `""` with `\"`
        jsonString = jsonString.replaceAll("\"\"\"", "\\\\\"");
        jsonString = jsonString.replaceAll("\"\"", "\\\\\"");

        // 3. Replace single quotes around values with double quotes
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

        // 4. Replace None with null
        jsonString = jsonString.replaceAll("\\bNone\\b", "null");

        // 5. Remove trailing commas before closing brackets
        jsonString = jsonString.replaceAll(",\\s*(\\}|\\])", "$1");

        // 6. Add commas between objects if missing
        jsonString = jsonString.replaceAll("}\\s*\\{", "},{");

        // 7. Ensure the string is a valid JSON array
        if (!jsonString.startsWith("[")) {
            jsonString = "[" + jsonString;
        }
        if (!jsonString.endsWith("]")) {
            jsonString = jsonString + "]";
        }

        return jsonString;
    }


}
