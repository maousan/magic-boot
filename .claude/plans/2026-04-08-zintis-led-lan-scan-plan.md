# zintis-led lan scan feature plan

## goal
- Add LAN scanning to discover LED devices and return:
  - ipAddress
  - deviceName (must start with `ESP32`)

## scope
- Add DTOs:
  - `LedLanScanRequest`
  - `LedLanScanResponse`
  - `LedLanDeviceInfo`
- Add service:
  - `LedLanScannerService.scan`
- Add API endpoint:
  - `POST /led/lan/scan`
- Add test coverage and update HTTP script.

## verification
- Run `mvn -pl magic-plugin-zintis-led package`.
- Copy jar to `plugins` directory.
