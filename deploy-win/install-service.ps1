param(
    [string]$ServiceName = "magic-boot-app",
    [string]$NssmPath = "nssm"
)

$ErrorActionPreference = "Stop"

$deployHome = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $deployHome "..")).Path
$setEnvPath = Join-Path $deployHome "set-env.bat"
$currentJar = Join-Path $deployHome "current\app.jar"
$logDir = Join-Path $projectRoot "logs"

if (!(Test-Path $setEnvPath)) {
    throw "未找到环境脚本: $setEnvPath"
}

if (!(Test-Path $currentJar)) {
    throw "未找到 current jar: $currentJar。请先执行 release.bat 完成一次发布。"
}

if (!(Get-Command $NssmPath -ErrorAction SilentlyContinue)) {
    throw "未找到 nssm 命令。请安装 NSSM，并将 nssm.exe 加入 PATH，或通过 -NssmPath 指定绝对路径。"
}

if (!(Test-Path $logDir)) {
    New-Item -Path $logDir -ItemType Directory | Out-Null
}

$appOut = Join-Path $logDir "service.out.log"
$appErr = Join-Path $logDir "service.err.log"
$javaExe = "java"

# 先删除已存在服务，避免参数残留
& $NssmPath stop $ServiceName | Out-Null
& $NssmPath remove $ServiceName confirm | Out-Null

$args = "-jar `"$currentJar`""

& $NssmPath install $ServiceName $javaExe $args | Out-Null
& $NssmPath set $ServiceName AppDirectory $projectRoot | Out-Null
& $NssmPath set $ServiceName AppEnvironmentExtra `
    "SPRING_PROFILES_ACTIVE=dev" `
    "SPRING_FLYWAY_ENABLED=true" `
    "APP_PORT=8089" `
    "EXTEND_REDIS_ENABLED=true" `
    "SPRING_REDIS_HOST=127.0.0.1" `
    "SPRING_REDIS_PORT=6379" `
    "SPRING_REDIS_PASSWORD=Magic@Redis2026" `
    "SPRING_REDIS_DATABASE=0" `
    "SPRING_DATA_REDIS_HOST=127.0.0.1" `
    "SPRING_DATA_REDIS_PORT=6379" `
    "SPRING_DATA_REDIS_PASSWORD=Magic@Redis2026" `
    "SPRING_DATA_REDIS_DATABASE=0" `
    "SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/magic-boot?useSSL=false&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF8&autoReconnect=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" `
    "SPRING_DATASOURCE_USERNAME=magic" `
    "SPRING_DATASOURCE_PASSWORD=Magic@2026" `
    "TIGA_ENGINE_ENABLED=false" `
    "TIGA_MAGIC_TIMEOUT_MS=30000" | Out-Null

& $NssmPath set $ServiceName AppStdout $appOut | Out-Null
& $NssmPath set $ServiceName AppStderr $appErr | Out-Null
& $NssmPath set $ServiceName AppRotateFiles 1 | Out-Null
& $NssmPath set $ServiceName AppRotateOnline 1 | Out-Null
& $NssmPath set $ServiceName AppRotateBytes 10485760 | Out-Null
& $NssmPath set $ServiceName Start SERVICE_AUTO_START | Out-Null

& $NssmPath start $ServiceName | Out-Null

Write-Host "[OK] 服务安装并启动成功: $ServiceName"
Write-Host "[INFO] Jar: $currentJar"
Write-Host "[INFO] 健康检查: http://127.0.0.1:8089/actuator/health"
