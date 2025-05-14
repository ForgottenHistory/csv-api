@echo off
echo Starting CSV API application...
echo.
echo The application will be available at: http://localhost:8080/api/data
echo To limit results, use: http://localhost:8080/api/data?limit=2
echo.
echo Press Ctrl+C to stop the application
echo.
call mvn spring-boot:run
pause