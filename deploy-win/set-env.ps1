$ErrorActionPreference = "Stop"

$deployHome = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $deployHome "..")).Path

function Set-DefaultEnv {
    param(
        [string]$Name,
        [string]$Value
    )
    $current = [Environment]::GetEnvironmentVariable($Name, "Process")
    if (-not [string]::IsNullOrEmpty($current)) {
        return
    }
    [Environment]::SetEnvironmentVariable($Name, $Value, "Process")
}

Set-DefaultEnv "PROJECT_ROOT" $projectRoot
Set-DefaultEnv "JAVA_BIN" "java"
Set-DefaultEnv "JAVA_OPTS" "-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

Set-DefaultEnv "APP_PORT" "8089"
Set-DefaultEnv "SPRING_PROFILES_ACTIVE" "dev"
Set-DefaultEnv "SPRING_FLYWAY_ENABLED" "true"
Set-DefaultEnv "UPLOAD_DIR" (Join-Path $projectRoot "uploads")
Set-DefaultEnv "LOG_DIR" (Join-Path $projectRoot "logs")
Set-DefaultEnv "HEALTH_CHECK_PATH" "/actuator/health"
Set-DefaultEnv "HEALTH_CHECK_RETRIES" "20"
Set-DefaultEnv "HEALTH_CHECK_INTERVAL_SEC" "3"
Set-DefaultEnv "HEALTH_CHECK_TIMEOUT_SEC" "3"

Set-DefaultEnv "SPRING_DATASOURCE_URL" "jdbc:mysql://127.0.0.1:3306/magic-boot?useSSL=false&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF8&autoReconnect=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai"
Set-DefaultEnv "SPRING_DATASOURCE_USERNAME" "magic"
Set-DefaultEnv "SPRING_DATASOURCE_PASSWORD" "Magic@2026"

Set-DefaultEnv "EXTEND_REDIS_ENABLED" "true"
Set-DefaultEnv "SPRING_DATA_REDIS_HOST" "127.0.0.1"
Set-DefaultEnv "SPRING_DATA_REDIS_PORT" "6379"
Set-DefaultEnv "SPRING_DATA_REDIS_PASSWORD" "Magic@Redis2026"
Set-DefaultEnv "SPRING_DATA_REDIS_DATABASE" "0"

Set-DefaultEnv "SPRING_REDIS_HOST" $env:SPRING_DATA_REDIS_HOST
Set-DefaultEnv "SPRING_REDIS_PORT" $env:SPRING_DATA_REDIS_PORT
Set-DefaultEnv "SPRING_REDIS_PASSWORD" $env:SPRING_DATA_REDIS_PASSWORD
Set-DefaultEnv "SPRING_REDIS_DATABASE" $env:SPRING_DATA_REDIS_DATABASE

Set-DefaultEnv "TIGA_ENGINE_ENABLED" "false"
Set-DefaultEnv "TIGA_MAGIC_TIMEOUT_MS" "30000"

$localOverride = Join-Path $deployHome "set-env.local.ps1"
if (Test-Path $localOverride) {
    . $localOverride
}

Write-Host "[OK] Environment loaded into current PowerShell session."
Write-Host "[INFO] APP_PORT=$env:APP_PORT, JAVA_BIN=$env:JAVA_BIN, PROFILE=$env:SPRING_PROFILES_ACTIVE"
