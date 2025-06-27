@echo off
setlocal enabledelayedexpansion

echo NCR 7167 Printer - EXE Creation Script
echo =====================================

:: Set Java 8 JDK path
set JAVA8_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.392.8-hotspot
set JAVAC_CMD="%JAVA8_HOME%\bin\javac.exe"
set JAVA_CMD="%JAVA8_HOME%\bin\java.exe"
set JAR_CMD="%JAVA8_HOME%\bin\jar.exe"

:: Check if we have Java 8 JDK
if not exist %JAVAC_CMD% (
    echo ERROR: Java 8 JDK not found at %JAVA8_HOME%
    echo Please install Java 8 JDK or update the path
    pause
    exit /b 1
)

:: Project paths
set PROJECT_ROOT=%~dp0
set SRC_DIR=%PROJECT_ROOT%src\main\java
set BUILD_DIR=%PROJECT_ROOT%build
set CLASSES_DIR=%BUILD_DIR%\classes
set LIB_DIR=%PROJECT_ROOT%lib
set DIST_DIR=%PROJECT_ROOT%dist

:: Create directories
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"
if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"
if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"

echo.
echo Step 1: Compiling Java sources...
echo ---------------------------------

:: Build classpath
set CLASSPATH=
for %%f in ("%LIB_DIR%\*.jar") do (
    if "!CLASSPATH!"=="" (
        set CLASSPATH=%%f
    ) else (
        set CLASSPATH=!CLASSPATH!;%%f
    )
)

:: Find all Java files
set JAVA_FILES=
for /r "%SRC_DIR%" %%f in (*.java) do (
    set JAVA_FILES=!JAVA_FILES! "%%f"
)

:: Compile
%JAVAC_CMD% -cp "!CLASSPATH!" -d "%CLASSES_DIR%" -encoding UTF-8 -source 1.8 -target 1.8 !JAVA_FILES!

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!

echo.
echo Step 2: Creating JAR file...
echo ---------------------------

:: Copy resources
if exist "%PROJECT_ROOT%src\main\resources" (
    xcopy /E /I /Y "%PROJECT_ROOT%src\main\resources\*" "%CLASSES_DIR%"
)

:: Create manifest file
echo Main-Class: com.ncr.printer.examples.PrinterExample > "%BUILD_DIR%\MANIFEST.MF"
echo Class-Path: . jSerialComm-2.9.3.jar slf4j-api-1.7.36.jar logback-classic-1.2.12.jar logback-core-1.2.12.jar >> "%BUILD_DIR%\MANIFEST.MF"

:: Create JAR
%JAR_CMD% cfm "%DIST_DIR%\ncr7167-printer.jar" "%BUILD_DIR%\MANIFEST.MF" -C "%CLASSES_DIR%" .

if %errorlevel% neq 0 (
    echo JAR creation failed!
    pause
    exit /b 1
)

echo JAR file created: %DIST_DIR%\ncr7167-printer.jar

echo.
echo Step 3: Copying dependencies...
echo ------------------------------

:: Copy library JARs to dist directory
copy "%LIB_DIR%\*.jar" "%DIST_DIR%\"

echo.
echo Step 4: Creating launcher script...
echo ---------------------------------

:: Create a simple launcher script
echo @echo off > "%DIST_DIR%\run-ncr7167.bat"
echo cd /d "%%~dp0" >> "%DIST_DIR%\run-ncr7167.bat"
echo java -jar ncr7167-printer.jar >> "%DIST_DIR%\run-ncr7167.bat"
echo pause >> "%DIST_DIR%\run-ncr7167.bat"

:: Create simple example launcher
echo @echo off > "%DIST_DIR%\run-simple-example.bat"
echo cd /d "%%~dp0" >> "%DIST_DIR%\run-simple-example.bat"
echo java -cp "ncr7167-printer.jar;*.jar" com.ncr.printer.examples.SimpleExample >> "%DIST_DIR%\run-simple-example.bat"
echo pause >> "%DIST_DIR%\run-simple-example.bat"

echo.
echo Step 5: Testing JAR file...
echo --------------------------

echo Testing SimpleExample...
cd "%DIST_DIR%"
%JAVA_CMD% -cp "ncr7167-printer.jar;*.jar" com.ncr.printer.examples.SimpleExample

echo.
echo ===========================================
echo Build completed successfully!
echo ===========================================
echo.
echo Files created in %DIST_DIR%:
echo - ncr7167-printer.jar (Main application)
echo - *.jar (Dependencies)
echo - run-ncr7167.bat (Interactive example launcher)
echo - run-simple-example.bat (Simple example launcher)
echo.
echo To run the application on any Windows machine:
echo 1. Copy the 'dist' folder to target machine
echo 2. Make sure Java 8+ is installed
echo 3. Double-click run-ncr7167.bat or run-simple-example.bat
echo.
pause
