# CSV API

A REST API that reads data from a CSV file and returns it in JSON format via a GET endpoint.

## Features

- GET endpoint that returns data from a CSV file in JSON format
- Optional query parameter to limit the number of returned records
- Error handling:
  - 400 Bad Request if limit is not a positive integer
  - 204 No Content if no data is found
  - 500 Internal Server Error if the file is missing or invalid

## How to Build & Run

**Install & run bat files have been included. Install then run.**

**REQUIREMENTS: JAVA 21 & MAVEN**

To manually build the application, run in terminal at root folder:

```bash
mvn clean install
```
Start:

```bash
bashmvn spring-boot:run
```

Application will be on port 8080 by default.

Use HTTP GET requests to receive data.
```
// Get all items
GET http://localhost:8080/api/data

// Limit to 2 first items
GET http://localhost:8080/api/data?limit=2
```

**Example Response**
```json
[
  {
    "id": 1,
    "name": "Alice",
    "age": 28,
    "email": "alice@example.com"
  },
  {
    "id": 2,
    "name": "Bob",
    "age": 35,
    "email": "bob@example.com"
  }
]
```

## CSV Format

The API expects a CSV file with the following format:

```csv
id,name,age,email
1,Alice,28,alice@example.com
2,Bob,35,bob@example.com
3,Charlie,40,charlie@example.com
```

Default location for the CSV file is src/main/resources/data.csv.

## Configuration

You can configure the location of the CSV file by setting the csv.file.path property in application.properties:

propertiescsv.file.path=src/main/resources/custom-data.csv

## Technologies Used

- Java 21
- Spring Boot
- Apache Commons CSV
- Maven
