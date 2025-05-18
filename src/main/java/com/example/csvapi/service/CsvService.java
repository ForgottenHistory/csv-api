package com.example.csvapi.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.csvapi.model.Person;
import com.example.csvapi.util.CsvParser;

////////////////////////////////////////////////////////////////////////////////////////////

@Service  // Make sure this annotation is present
public class CsvService {

    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);

    private final CsvParser csvParser;
    private final String csvFilePath;

    ////////////////////////////////////////////////////////////////////////////////////////////
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

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Get data from the CSV file, optionally limited to a specified number of
     * records.
     *
     * @param limit The maximum number of records to return, or null for all
     * records
     * @return A list of Person objects
     * @throws IOException If there is an error reading or parsing the CSV file
     */
    
    public List<Person> getData(Integer limit) throws IOException {
        logger.debug("Retrieving data with limit: {}", limit);

        // Get all data from the CSV file
        long startTime = System.currentTimeMillis();
        List<Person> allData = csvParser.parseCsvFile(csvFilePath);
        long parseTime = System.currentTimeMillis() - startTime;

        logger.info("Retrieved {} records in {}ms", allData.size(), parseTime);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // If no limit is specified or the limit is invalid, return all data
        if (limit == null || limit <= 0) {
            logger.debug("Returning all {} records (no valid limit specified)", allData.size());
            return allData;
        }

        // Otherwise, return only the specified number of records
        List<Person> limitedData = allData.stream()
                .limit(limit)
                .collect(Collectors.toList());
                
        logger.debug("Returning {} of {} total records (limit={})", limitedData.size(), allData.size(), limit);
        return limitedData;
    }


////////////////////////////////////////////////////////////////////////////////////////////

}
