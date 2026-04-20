$ErrorActionPreference = "Stop"

$deployHome = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $deployHome "..")).Path

. (Join-Path $deployHome "set-env.ps1")

function Test-ProcessAlive {
    param([int]$Pid)
    try {
        $p = Get-Process -Id $Pid -ErrorAction Stop
        return $null -ne $p
    }
    catch {
        return $false
    }
}

if ($env:JAVA_BIN -eq "java") {
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        throw "java not found. Install JDK 17 and configure PATH/JAVA_HOME."
    }
    $javaCmd = "java"
}
else {
    $javaCmd = $env:JAVA_BIN
    if (Test-Path $javaCmd -PathType Container) {
        $javaCmd = Join-Path $javaCmd "java.exe"
    }
    if (Test-Path $javaCmd -PathType Leaf) {
        # absolute java path is valid
    }
    elseif (Get-Command $javaCmd -ErrorAction SilentlyContinue) {
        # command name is resolvable
    }
    else {
        throw "JAVA_BIN not found or invalid: $($env:JAVA_BIN)"
    }
}

$logDir = $env:LOG_DIR
if (-not (Test-Path $logDir)) {
    New-Item -Path $logDir -ItemType Directory | Out-Null
}

$runDir = Join-Path $projectRoot "run"
if (-not (Test-Path $runDir)) {
    New-Item -Path $runDir -ItemType Directory | Out-Null
}

$pidFile = Join-Path $runDir "app.pid"
$outLog = Join-Path $logDir "app.out.log"
$errLog = Join-Path $logDir "app.err.log"

if (Test-Path $pidFile) {
    $oldPid = (Get-Content $pidFile -ErrorAction SilentlyContinue | Select-Object -First 1).Trim()
    if ($oldPid -match "^\d+$" -and (Test-ProcessAlive -Pid ([int]$oldPid))) {
        Write-Host "[INFO] App is already running. PID=$oldPid"
        exit 0
    }
    Remove-Item -Path $pidFile -Force -ErrorAction SilentlyContinue
}

$currentJar = Join-Path $deployHome "current\app.jar"
$jarFile = $null
if (Test-Path $currentJar) {
    $jarFile = $currentJar
}
else {
    $jarFile = Get-ChildItem -Path $deployHome -Filter *.jar -File -ErrorAction SilentlyContinue |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}

if (-not $jarFile) {
    throw "No runnable jar found in $deployHome"
}

Write-Host "[INFO] Jar: $jarFile"
Write-Host "[INFO] Java bin: $javaCmd"
Write-Host "[INFO] Port: $($env:APP_PORT)"
Write-Host "[INFO] Spring profile: $($env:SPRING_PROFILES_ACTIVE)"

$javaArgs = @($env:JAVA_OPTS, "-jar", $jarFile)
$proc = Start-Process -FilePath $javaCmd `
    -ArgumentList $javaArgs `
    -WorkingDirectory $projectRoot `
    -RedirectStandardOutput $outLog `
    -RedirectStandardError $errLog `
    -PassThru

$proc.Id | Out-File -FilePath $pidFile -Encoding ascii -Force

$healthUrl = "http://127.0.0.1:$($env:APP_PORT)$($env:HEALTH_CHECK_PATH)"
$retries = [int]$env:HEALTH_CHECK_RETRIES
$intervalSec = [int]$env:HEALTH_CHECK_INTERVAL_SEC
$timeoutSec = [int]$env:HEALTH_CHECK_TIMEOUT_SEC

Write-Host "[INFO] Health check URL: $healthUrl"
Write-Host "[INFO] Health retries=$retries, interval=${intervalSec}s, timeout=${timeoutSec}s"

$ok = $false
for ($i = 1; $i -le $retries; $i++) {
    try {
        $r = Invoke-WebRequest -Uri $healthUrl -UseBasicParsing -TimeoutSec $timeoutSec
        if ($r.StatusCode -ge 200 -and $r.StatusCode -lt 300) {
            Write-Host "[HEALTH] PASS attempt $i/$retries status=$($r.StatusCode)"
            $ok = $true
            break
        }
        Write-Host "[HEALTH] WAIT attempt $i/$retries status=$($r.StatusCode)"
    }
    catch {
        Write-Host "[HEALTH] WAIT attempt $i/$retries err=$($_.Exception.Message)"
    }
    Start-Sleep -Seconds $intervalSec
}

if (-not $ok) {
    Write-Host "[ERROR] Health check failed after retries. Stopping process..."
    try {
        Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
    }
    catch {}
    Remove-Item -Path $pidFile -Force -ErrorAction SilentlyContinue
    Write-Host "[ERROR] Startup aborted."
    exit 2
}

Write-Host "[OK] App started."
Write-Host "[OK] Health URL: $healthUrl"
