package com.example.csvapi.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.example.csvapi.model.Person;

@Component
public class CsvParser {

    /**
     * Parses the CSV file and converts each row to a Person object
     *
     * @param fileName path to the CSV file
     * @return List of Person objects
     * @throws IOException if file is not found, empty, or cannot be parsed
     */
    public List<Person> parseCsvFile(String fileName) throws IOException {
        Path pathToFile = Paths.get(fileName);

        // 1. Check if file exists
        if (!Files.exists(pathToFile)) {
            throw new IOException("File not found: " + fileName);
        }

        // 2. Check if file is empty
        if (Files.size(pathToFile) == 0) {
            throw new IOException("File is empty: " + fileName);
        }

        try (Reader reader = Files.newBufferedReader(pathToFile); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                .setHeader() // Use first row as headers
                .setSkipHeaderRecord(true) // Skip the header record
                .setIgnoreHeaderCase(true) // Ignore case in headers
                .setTrim(true) // Trim whitespace from values
                .build())) {

            List<Person> people = new ArrayList<>();

            // Iterate through each row in the CSV file
            for (CSVRecord record : csvParser) {
                // Create a new Person object from the CSV record
                String name = record.get("name");
                String email = record.get("email");
                int id = Integer.parseInt(record.get("id"));
                int age = Integer.parseInt(record.get("age"));

                // Validate the data
                if (name == null || name.isEmpty()) {
                    throw new IOException("Invalid name in record: " + record);
                }
                if (email == null || email.isEmpty()) {
                    throw new IOException("Invalid email in record: " + record);
                }
                if (id <= 0) {
                    throw new IOException("Invalid id in record: " + record);
                }
                if (age <= 0) {
                    throw new IOException("Invalid age in record: " + record);
                }

                // Create a new Person object and add it to the list
                Person person = new Person(id, name, age, email);
                
                people.add(person);
            }
            return people;
        }
    }
}
