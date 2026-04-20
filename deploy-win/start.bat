@echo off
setlocal EnableDelayedExpansion

set "DEPLOY_HOME=%~dp0"
call "%DEPLOY_HOME%set-env.bat"
if errorlevel 1 (
  echo [ERROR] Failed to load environment variables
  exit /b 1
)

set "JAVA_CMD="
if /I "%JAVA_BIN%"=="java" (
  where java >nul 2>nul
  if errorlevel 1 (
    echo [ERROR] java not found. Install JDK 17 and configure PATH/JAVA_HOME
    exit /b 1
  )
  set "JAVA_CMD=java"
) else (
  set "JAVA_CMD=!JAVA_BIN:"=!"
  if exist "!JAVA_CMD!\NUL" (
    set "JAVA_CMD=!JAVA_CMD!\java.exe"
  )
  if exist "!JAVA_CMD!" (
    rem java executable path is valid
  ) else (
    where "!JAVA_CMD!" >nul 2>nul
    if errorlevel 1 (
      set "JAVA_CMD="
    )
  )
  if not defined JAVA_CMD (
    echo [ERROR] JAVA_BIN is invalid or java not found: !JAVA_BIN!
    echo [HINT] Set JAVA_BIN to java.exe path, e.g. D:\magic-app\jdk-17.0.12\bin\java.exe
    exit /b 1
  )
)

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%PROJECT_ROOT%\run" mkdir "%PROJECT_ROOT%\run"

set "PID_FILE=%PROJECT_ROOT%\run\app.pid"
set "JAR_DIR=%DEPLOY_HOME%"
set "CURRENT_JAR=%DEPLOY_HOME%current\app.jar"

if exist "%PID_FILE%" del /f /q "%PID_FILE%" >nul 2>nul

set "JAR_FILE="
if exist "%CURRENT_JAR%" (
  set "JAR_FILE=%CURRENT_JAR%"
  goto :jar_found
)

for /f "delims=" %%i in ('dir /b /a:-d /o:-d "%JAR_DIR%\*.jar" 2^>nul') do (
  set "JAR_FILE=%JAR_DIR%\%%i"
  goto :jar_found
)

echo [ERROR] No runnable jar found: %JAR_DIR%\*.jar
echo [HINT] Copy the built jar into deploy-win directory first
exit /b 1

:jar_found
echo [INFO] Jar: %JAR_FILE%
echo [INFO] Java bin: %JAVA_CMD%
echo [INFO] Port: %APP_PORT%
echo [INFO] Spring profile: %SPRING_PROFILES_ACTIVE%
echo [INFO] Working dir: %PROJECT_ROOT%

echo [INFO] Starting in current process...
"%JAVA_CMD%" %JAVA_OPTS% -jar "%JAR_FILE%"
set "JAVA_EXIT_CODE=%ERRORLEVEL%"
if not "%JAVA_EXIT_CODE%"=="0" (
  echo [ERROR] Java process exited with code %JAVA_EXIT_CODE%.
  exit /b %JAVA_EXIT_CODE%
)
exit /b 0
