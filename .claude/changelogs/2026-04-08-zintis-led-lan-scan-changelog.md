# 2026-04-08 zintis-led lan scan changelog

## added
- New LAN scan API:
  - `POST /led/lan/scan`
- New scan DTOs:
  - `LedLanScanRequest`
  - `LedLanScanResponse`
  - `LedLanDeviceInfo`
- New scanner service:
  - `LedLanScannerService`

## behavior
- Scan configured subnet host range.
- Query each host using existing system info command.
- Keep only devices whose `payloadAscii` starts with `ESP32`.
- Return `ipAddress` and `deviceName` for matched devices.

## test updates
- Added `LedLanScannerServiceTest`.
- Updated `http/test-magic-plugin-zintis-led.http` with LAN scan request example.
