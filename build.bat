@echo off
setlocal enabledelayedexpansion

:: NCR 7167 Printer Java Library - Build Script
:: This script compiles the project without requiring Maven or JDK

set PROJECT_ROOT=%~dp0
set SRC_DIR=%PROJECT_ROOT%src\main\java
set BUILD_DIR=%PROJECT_ROOT%build
set CLASSES_DIR=%BUILD_DIR%\classes
set LIB_DIR=%PROJECT_ROOT%lib

:: Set Java 8 JDK path
set JAVA8_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.392.8-hotspot
set JAVAC_CMD="%JAVA8_HOME%\bin\javac.exe"
set JAVA_CMD="%JAVA8_HOME%\bin\java.exe"

:: Create directories
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"
if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"
if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

:: Check if we have Java 8 JDK
if not exist %JAVAC_CMD% (
    echo ERROR: Java 8 JDK not found at %JAVA8_HOME%
    echo Falling back to system javac...
    set JAVAC_CMD=javac
    set JAVA_CMD=java
    where javac >nul 2>&1
    if !errorlevel! neq 0 (
        echo ERROR: javac not found in PATH
        echo Please install JDK or add javac to your PATH
        pause
        exit /b 1
    )
)

set ACTION=%1
if "%ACTION%"=="" set ACTION=compile

if "%ACTION%"=="deps" goto download_deps
if "%ACTION%"=="compile" goto compile
if "%ACTION%"=="run" goto run_app
if "%ACTION%"=="clean" goto clean
goto show_help

:download_deps
echo Downloading dependencies...
:: Note: This requires curl or another download tool
echo Please manually download the following JARs to the lib directory:
echo - jSerialComm-2.9.3.jar
echo - slf4j-api-1.7.36.jar  
echo - logback-classic-1.2.12.jar
echo - logback-core-1.2.12.jar
echo.
echo You can download them from Maven Central Repository:
echo https://repo1.maven.org/maven2/
goto :eof

:compile
echo Compiling Java sources...

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

if "!JAVA_FILES!"=="" (
    echo No Java files found in %SRC_DIR%
    goto :eof
)

:: Compile
echo %JAVAC_CMD% -cp "!CLASSPATH!" -d "%CLASSES_DIR%" -encoding UTF-8 -source 1.8 -target 1.8 !JAVA_FILES!
%JAVAC_CMD% -cp "!CLASSPATH!" -d "%CLASSES_DIR%" -encoding UTF-8 -source 1.8 -target 1.8 !JAVA_FILES!

if %errorlevel% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
    exit /b 1
)
goto :eof

:run_app
call :compile
if %errorlevel% neq 0 goto :eof

echo Running simple example...

:: Build runtime classpath
set RUN_CLASSPATH=%CLASSES_DIR%
for %%f in ("%LIB_DIR%\*.jar") do (
    set RUN_CLASSPATH=!RUN_CLASSPATH!;%%f
)

%JAVA_CMD% -cp "!RUN_CLASSPATH!" com.ncr.printer.examples.SimpleExample
goto :eof

:clean
echo Cleaning build directory...
if exist "%BUILD_DIR%" (
    rmdir /s /q "%BUILD_DIR%"
    echo Build directory cleaned
)
goto :eof

:show_help
echo NCR 7167 Printer Java Library Build Script
echo.
echo Usage: build.bat [action]
echo.
echo Actions:
echo   deps     - Show dependency download instructions
echo   compile  - Compile Java sources (default)
echo   run      - Compile and run SimpleExample
echo   clean    - Clean build directory
echo.
echo Examples:
echo   build.bat compile
echo   build.bat run
echo.
goto :eof
