---
name: redeploy-pf4j-plugin-windows
description: Use when rebuilding and redeploying any PF4J plugin jar in this project on Windows, especially when plugin jars are file-locked by running Java processes.
---

# Redeploy PF4J Plugin on Windows

## Overview
This skill standardizes plugin redeployment for the current project PF4J plugin system.
It applies to any plugin module that outputs a plugin jar and is loaded from `./plugins/`.

## When to Use
- You changed plugin code and need to redeploy
- `Copy-Item` fails with file lock (`FileSystemException`)
- You need a repeatable build-stop-replace-verify flow

## Required Inputs
- `pluginId`: from `<module>/src/main/resources/plugin.properties` (`plugin.id=...`)
- `moduleDir`: plugin module path (for example `magic-plugin-zintis-led`)
- `artifactJar`: built jar name in `<moduleDir>/target/`

## Step 0: Discover metadata

### 0.1 Read plugin id
```powershell
Get-Content -Raw "<workspace>\<moduleDir>\src\main\resources\plugin.properties"
```

### 0.2 Confirm output jar
```powershell
Get-ChildItem "<workspace>\<moduleDir>\target\*.jar" |
  Select-Object Name, Length, LastWriteTime
```

## Step 1: Build plugin jar
Run in module directory to avoid `-pl` ambiguity on Windows PowerShell.

```powershell
mvn -DskipTests package
```

## Step 2: Inspect Java processes
```powershell
Get-CimInstance Win32_Process |
  Where-Object { $_.Name -match '^java(\\.exe)?$' } |
  Select-Object ProcessId, Name, CommandLine
```

Pick the process that is running the main app (usually contains `MagicBootApplication` or `magic-boot.jar`).

## Step 3: Stop lock holder process
```powershell
Stop-Process -Id <PID> -Force
```

## Step 4: Replace plugin jar
```powershell
Copy-Item -Force `
  "<workspace>\<moduleDir>\target\<artifactJar>" `
  "<workspace>\plugins\<artifactJar>"
```

## Step 5: Verify replacement
```powershell
Get-Item "<workspace>\plugins\<artifactJar>" |
  Select-Object FullName, Length, LastWriteTime
```

## Step 6: Restart and runtime verification
1. Restart main app.
2. Verify plugin admin APIs:
   - `GET /plugin/admin/list`
   - `GET /plugin/admin/health/{pluginId}`
3. Verify plugin business endpoints respond.

## Optional: Reload without full restart
If your runtime supports stable reload for current plugin:
- `POST /plugin/admin/reload/{pluginId}`

Use only when reload behavior is confirmed safe in your environment.

## One-Command Template (after PID confirmed)
```powershell
Stop-Process -Id <PID> -Force; `
Copy-Item -Force "<workspace>\<moduleDir>\target\<artifactJar>" "<workspace>\plugins\<artifactJar>"; `
Get-Item "<workspace>\plugins\<artifactJar>" | Select-Object FullName,Length,LastWriteTime
```

## Common Mistakes
- Building from repo root with unstable `-pl` usage in PowerShell.
  - Fix: run Maven inside module directory.
- Replacing plugin jar while app is still running.
  - Fix: stop lock holder Java process first.
- Killing wrong Java process.
  - Fix: confirm by `CommandLine` before `Stop-Process`.
- Using wrong plugin id for admin reload/health.
  - Fix: read `plugin.properties` first.

## Success Criteria
- Maven build returns `BUILD SUCCESS`
- `plugins/<artifactJar>` timestamp updates
- Main app restarts (or plugin reloads) successfully
- `/plugin/admin/health/{pluginId}` is normal
- Target plugin APIs behave as expected
