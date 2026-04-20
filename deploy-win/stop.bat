@echo off
setlocal

set "DEPLOY_HOME=%~dp0"
for %%i in ("%DEPLOY_HOME%..") do set "PROJECT_ROOT=%%~fi"
set "PID_FILE=%PROJECT_ROOT%\run\app.pid"

if not exist "%PID_FILE%" (
  echo [WARN] PID file not found: %PID_FILE%
  echo [HINT] If process still runs, use: tasklist ^| findstr java
  exit /b 0
)

set /p PID=<"%PID_FILE%"
if "%PID%"=="" (
  echo [WARN] PID file is empty, removed
  del /f /q "%PID_FILE%" >nul 2>nul
  exit /b 0
)

tasklist /FI "PID eq %PID%" | findstr /R /C:" %PID% " >nul
if errorlevel 1 (
  echo [WARN] PID=%PID% does not exist, cleaning PID file
  del /f /q "%PID_FILE%" >nul 2>nul
  exit /b 0
)

taskkill /PID %PID% /T /F >nul 2>nul
if errorlevel 1 (
  echo [ERROR] Failed to stop PID=%PID%
  exit /b 1
)

del /f /q "%PID_FILE%" >nul 2>nul
echo [OK] App stopped. PID=%PID%
exit /b 0
