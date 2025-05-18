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

    public List<Person> parseCsvFile(String fileName) throws IOException {
        logger.debug("Attempting to parse CSV file: {}", fileName);
        Path pathToFile = Paths.get(fileName);

        validateFile(pathToFile, fileName);

        try (Reader reader = Files.newBufferedReader(pathToFile); 
            CSVParser csvParser = createCsvParser(reader)) {

            logger.info("CSV file opened successfully: {}", fileName);
            List<Person> people = parseRecords(csvParser);

            logger.info("Successfully parsed {} records from CSV file: {}", people.size(), fileName);
            return people;
        } catch (Exception e) {
            logger.error("Error parsing CSV file: {}", fileName, e);
            throw new IOException("Error parsing CSV file: " + fileName, e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Validates that the file exists and is not empty
     * 
     * @param pathToFile file path to validate
     * @param fileName file name for error messages
     * @throws IOException if validation fails
     */

    private void validateFile(Path pathToFile, String fileName) throws IOException {
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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a configured CSV parser
     * 
     * @param reader the file reader
     * @return configured CSVParser
     * @throws IOException if parser creation fails
     */
    
    private CSVParser createCsvParser(Reader reader) throws IOException {
        return new CSVParser(reader, CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Parses all records from the CSV parser
     * 
     * @param csvParser the configured parser
     * @return list of Person objects
     * @throws IOException if parsing fails
     */

    private List<Person> parseRecords(CSVParser csvParser) throws IOException {
        List<Person> people = new ArrayList<>();

        for (CSVRecord record : csvParser) {
            Person person = parseRecord(record);
            people.add(person);
        }

        return people;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Parses a single CSV record into a Person object
     * 
     * @param record the CSV record
     * @return Person object
     * @throws IOException if record validation fails
     */

    private Person parseRecord(CSVRecord record) throws IOException {
        try {
            String name = record.get("name");
            String email = record.get("email");
            int id = Integer.parseInt(record.get("id"));
            int age = Integer.parseInt(record.get("age"));

            validateRecord(record, name, email, id, age);

            Person person = new Person(id, name, age, email);
            logger.debug("Parsed person record: id={}, name={}, age={}", id, name, age);
            return person;

        } catch (NumberFormatException e) {
            logger.error("Failed to parse numeric field in record: {}", record, e);
            throw new IOException("Failed to parse numeric field in record: " + record, e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Validates a CSV record's fields. NOTE: If parameters are to be added, best to create a class to hold them.
     * 
     * @param record the CSV record (for error reporting)
     * @param name the name field
     * @param email the email field
     * @param id the id field
     * @param age the age field
     * @throws IOException if validation fails
     */

    private void validateRecord(CSVRecord record, String name, String email, int id, int age) throws IOException {
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
    }

////////////////////////////////////////////////////////////////////////////////////////////

}
