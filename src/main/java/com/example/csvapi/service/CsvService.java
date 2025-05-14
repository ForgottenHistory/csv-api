package com.example.csvapi.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import com.example.csvapi.model.Person;
import com.example.csvapi.util.CsvParser;

public class CsvService {

    private final CsvParser csvParser;
    private final String csvFilePath;

    /**
     * Constructor for CsvService.
     *
     * @param csvParser The CSV parser used to read the CSV file
     * @param csvFilePath The path to the CSV file, injected from application
     * properties
     */
    public CsvService(CsvParser csvParser, @Value("${csv.file.path:src/main/resources/data.csv}") String csvFilePath) {
        this.csvParser = csvParser;
        this.csvFilePath = csvFilePath;
    }

    /**
     * Get data from the CSV file, optionally limited to a specified number of
     * records.
     *
     * @param limit The maximum number of records to return, or null for all
     * records
     * @return A list of Person objects
     * @throws IOException If there is an error reading or parsing the CSV file
     */
    public List<Person> getData(Integer limit) throws IOException 
    {
        // Get all data from the CSV file
        List<Person> allData = csvParser.parseCsvFile(csvFilePath);
        
        // If no limit is specified or the limit is invalid, return all data
        if (limit == null || limit <= 0) {
            return allData;
        }
        
        // Otherwise, return only the specified number of records
        return allData.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}
