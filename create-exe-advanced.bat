@echo off
echo Creating standalone Windows executable with jpackage...
echo ========================================================

:: Set Java paths
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.0.35-hotspot
set JPACKAGE_CMD="%JAVA_HOME%\bin\jpackage.exe"

:: Check if jpackage exists (Java 14+)
if not exist %JPACKAGE_CMD% (
    echo ERROR: jpackage not found. This requires Java 14+ with jpackage tool.
    echo Trying alternative approach with Launch4j...
    goto launch4j_approach
)

:: Project paths
set PROJECT_ROOT=%~dp0..
set DIST_DIR=%PROJECT_ROOT%\dist
set OUTPUT_DIR=%PROJECT_ROOT%\exe-output

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

echo.
echo Creating Windows executable with jpackage...
cd "%DIST_DIR%"

%JPACKAGE_CMD% ^
    --input . ^
    --dest "%OUTPUT_DIR%" ^
    --name "NCR7167-Printer" ^
    --main-jar ncr7167-printer.jar ^
    --main-class com.ncr.printer.examples.PrinterExample ^
    --type exe ^
    --win-console ^
    --app-version 1.0 ^
    --description "NCR 7167 Two-Station POS Printer Library" ^
    --vendor "NCR Printer Library"

if %errorlevel% equ 0 (
    echo.
    echo SUCCESS: Executable created in %OUTPUT_DIR%
    goto end
)

:launch4j_approach
echo.
echo Alternative: Using portable launcher approach...
echo ==============================================

:: Create a more advanced launcher that includes Java detection
echo @echo off > "%DIST_DIR%\NCR7167-Printer.bat"
echo setlocal >> "%DIST_DIR%\NCR7167-Printer.bat"
echo echo NCR 7167 Printer - Starting Application... >> "%DIST_DIR%\NCR7167-Printer.bat"
echo echo. >> "%DIST_DIR%\NCR7167-Printer.bat"
echo. >> "%DIST_DIR%\NCR7167-Printer.bat"
echo :: Check for Java >> "%DIST_DIR%\NCR7167-Printer.bat"
echo java -version ^>nul 2^>^&1 >> "%DIST_DIR%\NCR7167-Printer.bat"
echo if %%errorlevel%% neq 0 ^( >> "%DIST_DIR%\NCR7167-Printer.bat"
echo     echo ERROR: Java is not installed or not in PATH >> "%DIST_DIR%\NCR7167-Printer.bat"
echo     echo Please install Java 8 or higher >> "%DIST_DIR%\NCR7167-Printer.bat"
echo     pause >> "%DIST_DIR%\NCR7167-Printer.bat"
echo     exit /b 1 >> "%DIST_DIR%\NCR7167-Printer.bat"
echo ^) >> "%DIST_DIR%\NCR7167-Printer.bat"
echo. >> "%DIST_DIR%\NCR7167-Printer.bat"
echo :: Change to application directory >> "%DIST_DIR%\NCR7167-Printer.bat"
echo cd /d "%%~dp0" >> "%DIST_DIR%\NCR7167-Printer.bat"
echo. >> "%DIST_DIR%\NCR7167-Printer.bat"
echo :: Run the application >> "%DIST_DIR%\NCR7167-Printer.bat"
echo echo Starting NCR 7167 Printer Application... >> "%DIST_DIR%\NCR7167-Printer.bat"
echo java -cp "ncr7167-printer.jar;*.jar" com.ncr.printer.examples.PrinterExample >> "%DIST_DIR%\NCR7167-Printer.bat"
echo. >> "%DIST_DIR%\NCR7167-Printer.bat"
echo if %%errorlevel%% neq 0 ^( >> "%DIST_DIR%\NCR7167-Printer.bat"
echo     echo. >> "%DIST_DIR%\NCR7167-Printer.bat"
echo     echo Application exited with error code %%errorlevel%% >> "%DIST_DIR%\NCR7167-Printer.bat"
echo     pause >> "%DIST_DIR%\NCR7167-Printer.bat"
echo ^) >> "%DIST_DIR%\NCR7167-Printer.bat"

:: Create simple example launcher
echo @echo off > "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo setlocal >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo echo NCR 7167 Simple Example - Starting... >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo echo. >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo. >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo :: Check for Java >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo java -version ^>nul 2^>^&1 >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo if %%errorlevel%% neq 0 ^( >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo     echo ERROR: Java is not installed or not in PATH >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo     echo Please install Java 8 or higher >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo     pause >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo     exit /b 1 >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo ^) >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo. >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo cd /d "%%~dp0" >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo echo Starting Simple Example... >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo java -cp "ncr7167-printer.jar;*.jar" com.ncr.printer.examples.SimpleExample >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo echo. >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo echo Example completed, press any key to exit... >> "%DIST_DIR%\NCR7167-SimpleExample.bat"
echo pause ^>nul >> "%DIST_DIR%\NCR7167-SimpleExample.bat"

echo.
echo SUCCESS: Advanced launcher scripts created!
echo.
echo Files created in %DIST_DIR%:
echo - NCR7167-Printer.bat (Interactive example launcher)
echo - NCR7167-SimpleExample.bat (Simple example launcher)
echo - ncr7167-printer.jar (Main application)
echo - *.jar (Dependencies)

:end
echo.
echo ===========================================
echo Deployment package ready!
echo ===========================================
echo.
echo To deploy to target machine:
echo 1. Copy the entire 'dist' folder
echo 2. Ensure Java 8+ is installed on target machine
echo 3. Double-click NCR7167-Printer.bat or NCR7167-SimpleExample.bat
echo.
echo The application will automatically detect Java and run.
echo.
pause
