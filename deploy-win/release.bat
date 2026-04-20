@echo off
setlocal EnableDelayedExpansion

set "DEPLOY_HOME=%~dp0"
for %%i in ("%DEPLOY_HOME%..") do set "PROJECT_ROOT=%%~fi"
set "RELEASES_DIR=%DEPLOY_HOME%releases"
set "CURRENT_DIR=%DEPLOY_HOME%current"
set "CURRENT_JAR=%CURRENT_DIR%\app.jar"
set "CURRENT_VER_FILE=%DEPLOY_HOME%current.version"
set "PREVIOUS_VER_FILE=%DEPLOY_HOME%previous.version"

if "%~1"=="" goto :usage
if /I "%~1"=="-h" goto :usage
if /I "%~1"=="--help" goto :usage

set "SOURCE_JAR=%~f1"
set "VERSION=%~2"

if not exist "%SOURCE_JAR%" (
  echo [ERROR] Source jar not found: %SOURCE_JAR%
  exit /b 1
)

if "%VERSION%"=="" (
  for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMddHHmmss"') do set "VERSION=%%i"
)

set "TARGET_RELEASE_DIR=%RELEASES_DIR%\%VERSION%"
set "TARGET_RELEASE_JAR=%TARGET_RELEASE_DIR%\app.jar"

if exist "%TARGET_RELEASE_DIR%" (
  echo [ERROR] Release version already exists: %VERSION%
  exit /b 1
)

if not exist "%RELEASES_DIR%" mkdir "%RELEASES_DIR%"
if not exist "%CURRENT_DIR%" mkdir "%CURRENT_DIR%"

set "OLD_VERSION="
if exist "%CURRENT_VER_FILE%" set /p OLD_VERSION=<"%CURRENT_VER_FILE%"

echo [INFO] Release version: %VERSION%
echo [INFO] Source jar: %SOURCE_JAR%
echo [INFO] Archive dir: %TARGET_RELEASE_DIR%

mkdir "%TARGET_RELEASE_DIR%" >nul 2>nul
copy /y "%SOURCE_JAR%" "%TARGET_RELEASE_JAR%" >nul
if errorlevel 1 (
  echo [ERROR] Failed to copy source jar to release dir
  exit /b 1
)

if defined OLD_VERSION (
  echo [INFO] Previous current version: %OLD_VERSION%
)

call "%DEPLOY_HOME%stop.bat"

copy /y "%TARGET_RELEASE_JAR%" "%CURRENT_JAR%" >nul
if errorlevel 1 (
  echo [ERROR] Failed to update current jar
  exit /b 1
)

> "%CURRENT_VER_FILE%" echo %VERSION%
if defined OLD_VERSION (
  > "%PREVIOUS_VER_FILE%" echo %OLD_VERSION%
)

call "%DEPLOY_HOME%start.bat"
if errorlevel 1 (
  echo [ERROR] Start failed on version %VERSION%
  if defined OLD_VERSION (
    echo [INFO] Auto rollback to previous version: %OLD_VERSION%
    call "%DEPLOY_HOME%rollback.bat" %OLD_VERSION%
    exit /b 2
  ) else (
    echo [WARN] No previous version available for rollback
    exit /b 2
  )
)

echo [OK] Release success. Current version: %VERSION%
exit /b 0

:usage
echo Usage:
echo   release.bat ^<source-jar-path^> [version]
echo Example:
echo   release.bat D:\build\magic-boot.jar 20260410153000
exit /b 1
