$ErrorActionPreference = "Stop"

$deployHome = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $deployHome "..")).Path
$pidFile = Join-Path $projectRoot "run\app.pid"

function Test-ProcessAlive {
    param([int]$ProcId)
    try {
        $p = Get-Process -Id $ProcId -ErrorAction Stop
        return $null -ne $p
    }
    catch {
        return $false
    }
}

if (-not (Test-Path $pidFile)) {
    Write-Host "[WARN] PID file not found: $pidFile"
    Write-Host "[HINT] If process still runs, use: Get-Process java"
    exit 0
}

$pidText = (Get-Content $pidFile -ErrorAction SilentlyContinue | Select-Object -First 1).Trim()
if (-not $pidText) {
    Write-Host "[WARN] PID file is empty, removed"
    Remove-Item -Path $pidFile -Force -ErrorAction SilentlyContinue
    exit 0
}

if ($pidText -notmatch "^\d+$") {
    Write-Host "[WARN] PID file content is invalid: $pidText"
    Remove-Item -Path $pidFile -Force -ErrorAction SilentlyContinue
    exit 0
}

$targetPid = [int]$pidText
if (-not (Test-ProcessAlive -ProcId $targetPid)) {
    Write-Host "[WARN] PID=$targetPid does not exist, cleaning PID file"
    Remove-Item -Path $pidFile -Force -ErrorAction SilentlyContinue
    exit 0
}

try {
    Stop-Process -Id $targetPid -Force -ErrorAction Stop
}
catch {
    Write-Host "[ERROR] Failed to stop PID=$targetPid. $_"
    exit 1
}

Remove-Item -Path $pidFile -Force -ErrorAction SilentlyContinue
Write-Host "[OK] App stopped. PID=$targetPid"
