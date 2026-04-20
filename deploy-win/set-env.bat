@echo off
setlocal

rem =========================================================
rem Magic Boot Windows non-docker environment variables
rem Copy this file to set-env.local.bat to override defaults
rem =========================================================

set "DEPLOY_HOME=%~dp0"
set "PROJECT_ROOT=%DEPLOY_HOME%"
if exist "%DEPLOY_HOME%..\pom.xml" if exist "%DEPLOY_HOME%..\magic-boot-master\" (
  for %%i in ("%DEPLOY_HOME%..") do set "PROJECT_ROOT=%%~fi"
)

rem ---- Java options ----
if not defined JAVA_BIN set "JAVA_BIN=java"
if not defined JAVA_OPTS set "JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

rem ---- App basics ----
if not defined APP_PORT set "APP_PORT=8089"
if not defined SPRING_PROFILES_ACTIVE set "SPRING_PROFILES_ACTIVE=dev"
if not defined SPRING_FLYWAY_ENABLED set "SPRING_FLYWAY_ENABLED=true"
if not defined UPLOAD_DIR set "UPLOAD_DIR=%PROJECT_ROOT%\uploads"
if not defined LOG_DIR set "LOG_DIR=%PROJECT_ROOT%\logs"
if not defined HEALTH_CHECK_PATH set "HEALTH_CHECK_PATH=/actuator/health"
if not defined HEALTH_CHECK_RETRIES set "HEALTH_CHECK_RETRIES=20"
if not defined HEALTH_CHECK_INTERVAL_SEC set "HEALTH_CHECK_INTERVAL_SEC=3"
if not defined HEALTH_CHECK_TIMEOUT_SEC set "HEALTH_CHECK_TIMEOUT_SEC=3"

rem ---- Database (non-docker) ----
if not defined SPRING_DATASOURCE_URL set "SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/magic-boot?useSSL=false&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF8&autoReconnect=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai"
if not defined SPRING_DATASOURCE_USERNAME set "SPRING_DATASOURCE_USERNAME=magic"
if not defined SPRING_DATASOURCE_PASSWORD set "SPRING_DATASOURCE_PASSWORD=Magic@2026"

rem ---- Redis (non-docker) ----
if not defined EXTEND_REDIS_ENABLED set "EXTEND_REDIS_ENABLED=true"
if not defined SPRING_DATA_REDIS_HOST set "SPRING_DATA_REDIS_HOST=127.0.0.1"
if not defined SPRING_DATA_REDIS_PORT set "SPRING_DATA_REDIS_PORT=6379"
if not defined SPRING_DATA_REDIS_PASSWORD set "SPRING_DATA_REDIS_PASSWORD=Magic@Redis2026"
if not defined SPRING_DATA_REDIS_DATABASE set "SPRING_DATA_REDIS_DATABASE=0"

rem ---- Compatible with Redisson spring.redis.* ----
if not defined SPRING_REDIS_HOST set "SPRING_REDIS_HOST=%SPRING_DATA_REDIS_HOST%"
if not defined SPRING_REDIS_PORT set "SPRING_REDIS_PORT=%SPRING_DATA_REDIS_PORT%"
if not defined SPRING_REDIS_PASSWORD set "SPRING_REDIS_PASSWORD=%SPRING_DATA_REDIS_PASSWORD%"
if not defined SPRING_REDIS_DATABASE set "SPRING_REDIS_DATABASE=%SPRING_DATA_REDIS_DATABASE%"

rem ---- Tiga engine (optional) ----
if not defined TIGA_ENGINE_ENABLED set "TIGA_ENGINE_ENABLED=false"
if not defined TIGA_MAGIC_TIMEOUT_MS set "TIGA_MAGIC_TIMEOUT_MS=30000"

rem ---- Local override hook ----
if exist "%DEPLOY_HOME%set-env.local.bat" (
  call "%DEPLOY_HOME%set-env.local.bat"
)

endlocal & (
  set "PROJECT_ROOT=%PROJECT_ROOT%"
  set "JAVA_BIN=%JAVA_BIN%"
  set "JAVA_OPTS=%JAVA_OPTS%"
  set "APP_PORT=%APP_PORT%"
  set "SPRING_PROFILES_ACTIVE=%SPRING_PROFILES_ACTIVE%"
  set "SPRING_FLYWAY_ENABLED=%SPRING_FLYWAY_ENABLED%"
  set "UPLOAD_DIR=%UPLOAD_DIR%"
  set "LOG_DIR=%LOG_DIR%"
  set "HEALTH_CHECK_PATH=%HEALTH_CHECK_PATH%"
  set "HEALTH_CHECK_RETRIES=%HEALTH_CHECK_RETRIES%"
  set "HEALTH_CHECK_INTERVAL_SEC=%HEALTH_CHECK_INTERVAL_SEC%"
  set "HEALTH_CHECK_TIMEOUT_SEC=%HEALTH_CHECK_TIMEOUT_SEC%"
  set "SPRING_DATASOURCE_URL=%SPRING_DATASOURCE_URL%"
  set "SPRING_DATASOURCE_USERNAME=%SPRING_DATASOURCE_USERNAME%"
  set "SPRING_DATASOURCE_PASSWORD=%SPRING_DATASOURCE_PASSWORD%"
  set "EXTEND_REDIS_ENABLED=%EXTEND_REDIS_ENABLED%"
  set "SPRING_DATA_REDIS_HOST=%SPRING_DATA_REDIS_HOST%"
  set "SPRING_DATA_REDIS_PORT=%SPRING_DATA_REDIS_PORT%"
  set "SPRING_DATA_REDIS_PASSWORD=%SPRING_DATA_REDIS_PASSWORD%"
  set "SPRING_DATA_REDIS_DATABASE=%SPRING_DATA_REDIS_DATABASE%"
  set "SPRING_REDIS_HOST=%SPRING_REDIS_HOST%"
  set "SPRING_REDIS_PORT=%SPRING_REDIS_PORT%"
  set "SPRING_REDIS_PASSWORD=%SPRING_REDIS_PASSWORD%"
  set "SPRING_REDIS_DATABASE=%SPRING_REDIS_DATABASE%"
  set "TIGA_ENGINE_ENABLED=%TIGA_ENGINE_ENABLED%"
  set "TIGA_MAGIC_TIMEOUT_MS=%TIGA_MAGIC_TIMEOUT_MS%"
)
