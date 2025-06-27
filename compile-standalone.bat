@echo off
:: Simple compilation script for standalone version

set STANDALONE_DIR=%~dp0src\main\java\com\ncr\printer\standalone
set BUILD_DIR=%~dp0build
set CLASSES_DIR=%BUILD_DIR%\standalone

:: Create build directory
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"
if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"

echo Compiling standalone version...

:: Compile the standalone class
javac -d "%CLASSES_DIR%" "%STANDALONE_DIR%\Ncr7167PrinterStandalone.java"

if %errorlevel% equ 0 (
    echo Compilation successful!
    echo.
    echo To run the standalone version:
    echo java -cp "%CLASSES_DIR%" com.ncr.printer.standalone.Ncr7167PrinterStandalone COM1
    echo.
    echo Replace COM1 with your actual COM port name
) else (
    echo Compilation failed!
    exit /b 1
)

pause
