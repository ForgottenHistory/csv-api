package com.example.csvapi.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.csvapi.model.Person;
import com.example.csvapi.service.CsvService;

////////////////////////////////////////////////////////////////////////////////////////////

@RestController
@RequestMapping("/api")
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    private final CsvService csvService;

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor for DataController.
     *
     * @param csvService The service that provides access to CSV data
     */

    public DataController(CsvService csvService) {
        this.csvService = csvService;
        logger.info("DataController initialized");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * GET endpoint to retrieve data from the CSV file.
     * @param limit Optional parameter to limit the number of records returned
     * @return ResponseEntity containing either the data or an error message
     */

    @GetMapping("/data")
    public ResponseEntity<?> getData(@RequestParam(required = false) Integer limit) {
        try {
            // Validate limit parameter if provided
            if (limit != null && limit <= 0) {
                logger.warn("Invalid limit parameter: {}", limit);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Limit must be a positive integer");
            }
            
            // Get data from service
            long startTime = System.currentTimeMillis();
            List<Person> data = csvService.getData(limit);
            long elapsedTime = System.currentTimeMillis() - startTime;
            
            // Return 204 No Content if no data found
            if (data.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            // Log performance metrics
            logger.info("Request processed successfully. Returned {} records in {}ms", data.size(), elapsedTime);

            // Return data with 200 OK status
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            // Return 500 Internal Server Error for any IO exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading CSV file: " + e.getMessage());
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////
}
