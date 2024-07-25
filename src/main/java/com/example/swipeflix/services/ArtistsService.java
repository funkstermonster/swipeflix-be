package com.example.swipeflix.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArtistsService {

    private final ObjectMapper objectMapper = new ObjectMapper();


    public void readCreditsCSVFile() {
        String csvFilePath = "/credits.csv";

        try (InputStream inputStream = ArtistsService.class.getResourceAsStream(csvFilePath);
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            List<String[]> records = reader.readAll();

            if (records.isEmpty()) {
                System.err.println("No records found in the CSV file.");
                return;
            }

            // Skip the header row
            boolean isFirstRow = true;
            for (String[] record : records) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip the header row
                }

                if (record.length < 3) {
                    System.err.println("Skipping record due to insufficient columns: " + String.join(",", record));
                    continue;
                }

                String jsonString = record[0]; // JSON-like string is in the first column
                String id = record[2]; // ID is in the third column

                if (jsonString == null || jsonString.trim().isEmpty()) {
                    System.err.println("Empty JSON string for record: " + String.join(",", record));
                    continue;
                }

                // Correct the JSON format
                String correctedJsonString = correctJsonString(jsonString);

                // Print the corrected JSON string for debugging
                System.out.println("Corrected JSON String: " + correctedJsonString);

                try {
                    // Parse the corrected JSON string
                    JsonNode rootNode = objectMapper.readTree(correctedJsonString);

                    if (rootNode.isArray() && !rootNode.isEmpty()) {
                        int count = 0;
                        for (JsonNode node : rootNode) {
                            if (count >= 4) break; // Only process the first 4 elements

                            // Extract fields
                            int castId = node.path("cast_id").asInt();
                            String character = node.path("character").asText();
                            String creditId = node.path("credit_id").asText();
                            int gender = node.path("gender").asInt();
                            int artistId = node.path("id").asInt();
                            String name = node.path("name").asText();
                            int order = node.path("order").asInt();
                            String profilePath = node.path("profile_path").asText(null);

                            // Print extracted fields
                            System.out.printf("cast_id: %d, character: %s, credit_id: %s, gender: %d, id: %d, name: %s, order: %d, profile_path: %s%n",
                                    castId, character, creditId, gender, artistId, name, order, profilePath);

                            count++;
                        }
                    } else {
                        System.err.println("Expected JSON array but got something else: " + correctedJsonString);
                    }

                    // Output the ID
                    System.out.println("ID from CSV: " + id);
                } catch (Exception e) {
                    System.err.println("Failed to parse JSON string: " + correctedJsonString);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String correctJsonString(String jsonString) {
        // Replace single quotes around field names with double quotes
        jsonString = jsonString.replaceAll("'([a-zA-Z_]+)'(?=\\s*:)", "\"$1\"");

        // Replace single quotes around values with double quotes
        jsonString = jsonString.replaceAll(":\\s*'([^']*)'", ": \"$1\"");

        // Escape double quotes within string values, specifically handling 'segment'
        Pattern pattern = Pattern.compile("(?<=:\")(.*?)(?=\")");
        Matcher matcher = pattern.matcher(jsonString);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement = matcher.group();
            // Only escape double quotes within the string value
            if (replacement.contains("\"")) {
                replacement = replacement.replace("\"", "\\\"");
            }
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        jsonString = sb.toString();

        // Replace None with null
        jsonString = jsonString.replaceAll("\\bNone\\b", "null");

        // Remove trailing commas before closing brackets
        jsonString = jsonString.replaceAll(",\\s*(\\}|\\])", "$1");

        // Add commas between objects if missing
        jsonString = jsonString.replaceAll("\\}\\s*\\{", "},{");

        // Ensure the string is a valid JSON array
        if (!jsonString.startsWith("[")) {
            jsonString = "[" + jsonString;
        }
        if (!jsonString.endsWith("]")) {
            jsonString = jsonString + "]";
        }

        return jsonString;
    }
}