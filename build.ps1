# NCR 7167 Printer Java Library - Build Script
# This script compiles the project without requiring Maven or JDK

param(
    [string]$Action = "compile",
    [string]$JavaHome = $env:JAVA_HOME
)

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$SrcDir = Join-Path $ProjectRoot "src\main\java"
$TestSrcDir = Join-Path $ProjectRoot "src\test\java"
$BuildDir = Join-Path $ProjectRoot "build"
$ClassesDir = Join-Path $BuildDir "classes"
$TestClassesDir = Join-Path $BuildDir "test-classes"
$LibDir = Join-Path $ProjectRoot "lib"

# Set Java 8 JDK path
$Java8Home = "C:\Program Files\Eclipse Adoptium\jdk-8.0.392.8-hotspot"
if (Test-Path $Java8Home) {
    $JavacCmd = Join-Path $Java8Home "bin\javac.exe"
    $JavaCmd = Join-Path $Java8Home "bin\java.exe"
    Write-Host "Using Java 8 JDK: $Java8Home"
} elseif ($JavaHome -and (Test-Path $JavaHome)) {
    $JavacCmd = Join-Path $JavaHome "bin\javac.exe"
    $JavaCmd = Join-Path $JavaHome "bin\java.exe"
    Write-Host "Using JAVA_HOME: $JavaHome"
} else {
    $JavacCmd = "javac"
    $JavaCmd = "java"
    Write-Host "Using system Java commands"
}

# Create directories
if (!(Test-Path $BuildDir)) { New-Item -ItemType Directory -Path $BuildDir }
if (!(Test-Path $ClassesDir)) { New-Item -ItemType Directory -Path $ClassesDir }
if (!(Test-Path $TestClassesDir)) { New-Item -ItemType Directory -Path $TestClassesDir }
if (!(Test-Path $LibDir)) { New-Item -ItemType Directory -Path $LibDir }

# Function to download dependencies
function Download-Dependencies {
    Write-Host "Downloading dependencies..."
    
    $dependencies = @(
        @{
            Name = "jSerialComm-2.9.3.jar"
            Url = "https://repo1.maven.org/maven2/com/fazecast/jSerialComm/2.9.3/jSerialComm-2.9.3.jar"
        },
        @{
            Name = "slf4j-api-1.7.36.jar"
            Url = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"
        },
        @{
            Name = "logback-classic-1.2.12.jar"
            Url = "https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.2.12/logback-classic-1.2.12.jar"
        },
        @{
            Name = "logback-core-1.2.12.jar"
            Url = "https://repo1.maven.org/maven2/ch/qos/logback/logback-core/1.2.12/logback-core-1.2.12.jar"
        }
    )
    
    foreach ($dep in $dependencies) {
        $filePath = Join-Path $LibDir $dep.Name
        if (!(Test-Path $filePath)) {
            Write-Host "Downloading $($dep.Name)..."
            try {
                Invoke-WebRequest -Uri $dep.Url -OutFile $filePath
                Write-Host "Downloaded $($dep.Name)"
            } catch {
                Write-Warning "Failed to download $($dep.Name): $($_.Exception.Message)"
            }
        } else {
            Write-Host "$($dep.Name) already exists"
        }
    }
}

# Function to compile Java sources
function Compile-Java {
    Write-Host "Compiling Java sources..."
    
    # Get all Java files
    $javaFiles = Get-ChildItem -Path $SrcDir -Filter "*.java" -Recurse
    
    if ($javaFiles.Count -eq 0) {
        Write-Warning "No Java files found in $SrcDir"
        return $false
    }
    
    # Build classpath
    $jars = Get-ChildItem -Path $LibDir -Filter "*.jar"
    $classpath = ($jars | ForEach-Object { $_.FullName }) -join ";"
    
    # Compile command
    $javaFilesList = ($javaFiles | ForEach-Object { $_.FullName }) -join " "
    
    # Prepare arguments for javac
    $javacArgs = @(
        "-cp", $classpath,
        "-d", $ClassesDir,
        "-encoding", "UTF-8",
        "-source", "1.8",
        "-target", "1.8"
    )
    $javacArgs += $javaFiles | ForEach-Object { $_.FullName }
    
    Write-Host "Executing: `"$JavacCmd`" $($javacArgs -join ' ')"
    
    try {
        $process = Start-Process -FilePath $JavacCmd -ArgumentList $javacArgs -Wait -NoNewWindow -PassThru
        if ($process.ExitCode -eq 0) {
            Write-Host "Compilation successful!"
            return $true
        } else {
            Write-Error "Compilation failed with exit code $($process.ExitCode)"
            return $false
        }
    } catch {
        Write-Error "Compilation failed: $($_.Exception.Message)"
        return $false
    }
}

# Function to create JAR file
function Create-Jar {
    Write-Host "Creating JAR file..."
    
    $jarFile = Join-Path $BuildDir "ncr7167-printer.jar"
    $manifestFile = Join-Path $BuildDir "MANIFEST.MF"
    
    # Create manifest
    @"
Manifest-Version: 1.0
Main-Class: com.ncr.printer.examples.PrinterExample
Implementation-Title: NCR 7167 Printer Java Library
Implementation-Version: 1.0-SNAPSHOT
"@ | Out-File -FilePath $manifestFile -Encoding ASCII
    
    # Create JAR
    $jarCmd = "jar cfm `"$jarFile`" `"$manifestFile`" -C `"$ClassesDir`" ."
    
    try {
        Invoke-Expression $jarCmd
        Write-Host "JAR file created: $jarFile"
        return $true
    } catch {
        Write-Error "JAR creation failed: $($_.Exception.Message)"
        return $false
    }
}

# Function to run the application
function Run-Application {
    param([string]$MainClass = "com.ncr.printer.examples.SimpleExample")
    
    Write-Host "Running application: $MainClass"
    
    # Build classpath
    $jars = Get-ChildItem -Path $LibDir -Filter "*.jar"
    $classpath = @($ClassesDir) + ($jars | ForEach-Object { $_.FullName }) -join ";"
    
    Write-Host "Running application: $MainClass"
    Write-Host "Classpath: $classpath"
    
    try {
        $process = Start-Process -FilePath $JavaCmd -ArgumentList @("-cp", $classpath, $MainClass) -Wait -NoNewWindow -PassThru
        if ($process.ExitCode -ne 0) {
            Write-Error "Execution failed with exit code $($process.ExitCode)"
        }
    } catch {
        Write-Error "Execution failed: $($_.Exception.Message)"
    }
}

# Main execution
switch ($Action.ToLower()) {
    "deps" {
        Download-Dependencies
    }
    "compile" {
        Download-Dependencies
        if (Compile-Java) {
            Write-Host "Build completed successfully!"
        }
    }
    "jar" {
        Download-Dependencies
        if (Compile-Java) {
            Create-Jar
        }
    }
    "run" {
        Download-Dependencies
        if (Compile-Java) {
            Run-Application
        }
    }
    "run-simple" {
        Download-Dependencies
        if (Compile-Java) {
            Run-Application -MainClass "com.ncr.printer.examples.SimpleExample"
        }
    }
    "run-full" {
        Download-Dependencies
        if (Compile-Java) {
            Run-Application -MainClass "com.ncr.printer.examples.PrinterExample"
        }
    }
    "clean" {
        if (Test-Path $BuildDir) {
            Remove-Item -Path $BuildDir -Recurse -Force
            Write-Host "Build directory cleaned"
        }
    }
    default {
        Write-Host @"
NCR 7167 Printer Java Library Build Script

Usage: .\build.ps1 [action]

Actions:
  deps      - Download dependencies only
  compile   - Download dependencies and compile (default)
  jar       - Compile and create JAR file
  run       - Compile and run SimpleExample
  run-simple - Compile and run SimpleExample  
  run-full  - Compile and run PrinterExample (interactive)
  clean     - Clean build directory

Examples:
  .\build.ps1 compile
  .\build.ps1 run-simple
  .\build.ps1 jar

"@
    }
}
