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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.csvapi.model.Person;

////////////////////////////////////////////////////////////////////////////////////////////
// CSV-PARSER
// This class is responsible for parsing a CSV file and converting each row into a Person object.
// It uses Apache Commons CSV library to handle the parsing.
// POTENTIAL EXCEPTIONS: Exceptionally long files might cause errors. Streaming, caching, etc might be
////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class CsvParser {

    private static final Logger logger = LoggerFactory.getLogger(CsvParser.class);

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Parses the CSV file and converts each row to a Person object
     *
     * @param fileName path to the CSV file
     * @return List of Person objects
     * @throws IOException if file is not found, empty, or cannot be parsed
     */
    ////////////////////////////////////////////////////////////////////////////////////////////
    
    public List<Person> parseCsvFile(String fileName) throws IOException {

        Path pathToFile = Paths.get(fileName);
        logger.debug("Attempting to parse CSV file: {}", fileName);

        // Check if file exists
        if (!Files.exists(pathToFile)) {
            logger.error("File not found: {}", fileName);
            throw new IOException("File not found: " + fileName);
        }

        // Check if file is empty
        if (Files.size(pathToFile) == 0) {
            logger.error("File is empty: {}", fileName);
            throw new IOException("File is empty: " + fileName);
        }

        try (Reader reader = Files.newBufferedReader(pathToFile); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                .setHeader() // Use first row as headers
                .setSkipHeaderRecord(true) // Skip the header record
                .setIgnoreHeaderCase(true) // Ignore case in headers
                .setTrim(true) // Trim whitespace from values
                .build())) {

            logger.info("CSV file opened successfully: {}", fileName);
            List<Person> people = new ArrayList<>();

            // Iterate through each row in the CSV file
            for (CSVRecord record : csvParser) {
                try {
                    // Create a new Person object from the CSV record
                    String name = record.get("name");
                    String email = record.get("email");
                    int id = Integer.parseInt(record.get("id"));
                    int age = Integer.parseInt(record.get("age"));

                    // Validate the data
                    if (name == null || name.isEmpty()) {
                        logger.warn("Invalid name in record: {}", record);
                        throw new IOException("Invalid name in record: " + record);
                    }
                    if (email == null || email.isEmpty()) {
                        logger.warn("Invalid email in record: {}", record);
                        throw new IOException("Invalid email in record: " + record);
                    }
                    if (id <= 0) {
                        logger.warn("Invalid id in record: {}", record);
                        throw new IOException("Invalid id in record: " + record);
                    }
                    if (age <= 0) {
                        logger.warn("Invalid age in record: {}", record);
                        throw new IOException("Invalid age in record: " + record);
                    }

                    // Create a new Person object and add it to the list
                    Person person = new Person(id, name, age, email);

                    people.add(person);
                    //logger.debug("Parsed person record: id={}, name={}, age={}", id, name, age);
                } catch (NumberFormatException e) {
                    logger.error("Failed to parse numeric field in record: {}", record, e);
                    throw new IOException("Failed to parse numeric field in record: " + record, e);
                }
            }

            logger.info("Successfully parsed {} records from CSV file: {}", people.size(), fileName);
            return people;
        }
    }
}
