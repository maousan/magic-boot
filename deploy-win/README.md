# Windows Non-Docker Deploy

## Scripts

- `set-env.bat` / `set-env.ps1`: load environment variables
- `start.bat` / `start.ps1`: start app with health-check retries
- `stop.bat` / `stop.ps1`: stop app by `run/app.pid`
- `release.bat`: release new version
- `rollback.bat`: rollback version
- `install-service.ps1`: install NSSM service

## PowerShell usage

```powershell
. .\deploy-win\set-env.ps1
.\deploy-win\start.ps1
.\deploy-win\stop.ps1
```

## CMD usage

```bat
deploy-win\start.bat
deploy-win\stop.bat
```
