@echo off
echo Building CSV API application...
call mvn clean install
echo.
if %ERRORLEVEL% == 0 (
    echo Build successful! You can now run the application using run.bat
) else (
    echo Build failed with error code %ERRORLEVEL%
)
pause