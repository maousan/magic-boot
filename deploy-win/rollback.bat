@echo off
setlocal EnableDelayedExpansion

set "DEPLOY_HOME=%~dp0"
for %%i in ("%DEPLOY_HOME%..") do set "PROJECT_ROOT=%%~fi"
set "RELEASES_DIR=%DEPLOY_HOME%releases"
set "CURRENT_DIR=%DEPLOY_HOME%current"
set "CURRENT_JAR=%CURRENT_DIR%\app.jar"
set "CURRENT_VER_FILE=%DEPLOY_HOME%current.version"
set "PREVIOUS_VER_FILE=%DEPLOY_HOME%previous.version"

set "TARGET_VERSION=%~1"
if /I "%TARGET_VERSION%"=="-h" goto :usage
if /I "%TARGET_VERSION%"=="--help" goto :usage

if "%TARGET_VERSION%"=="" (
  if exist "%PREVIOUS_VER_FILE%" (
    set /p TARGET_VERSION=<"%PREVIOUS_VER_FILE%"
  )
)

if "%TARGET_VERSION%"=="" (
  echo [ERROR] No target rollback version provided and previous.version is missing
  exit /b 1
)

set "TARGET_JAR=%RELEASES_DIR%\%TARGET_VERSION%\app.jar"
if not exist "%TARGET_JAR%" (
  echo [ERROR] Target release jar not found: %TARGET_JAR%
  exit /b 1
)

if not exist "%CURRENT_DIR%" mkdir "%CURRENT_DIR%"

set "OLD_VERSION="
if exist "%CURRENT_VER_FILE%" set /p OLD_VERSION=<"%CURRENT_VER_FILE%"

echo [INFO] Rollback target version: %TARGET_VERSION%
if defined OLD_VERSION echo [INFO] Current version before rollback: %OLD_VERSION%

call "%DEPLOY_HOME%stop.bat"

copy /y "%TARGET_JAR%" "%CURRENT_JAR%" >nul
if errorlevel 1 (
  echo [ERROR] Failed to restore target jar
  exit /b 1
)

> "%CURRENT_VER_FILE%" echo %TARGET_VERSION%
if defined OLD_VERSION (
  > "%PREVIOUS_VER_FILE%" echo %OLD_VERSION%
)

call "%DEPLOY_HOME%start.bat"
if errorlevel 1 (
  echo [ERROR] Rollback start failed on version %TARGET_VERSION%
  exit /b 2
)

echo [OK] Rollback success. Current version: %TARGET_VERSION%
exit /b 0

:usage
echo Usage:
echo   rollback.bat [version]
echo Example:
echo   rollback.bat 20260410153000
echo   rollback.bat
exit /b 1
