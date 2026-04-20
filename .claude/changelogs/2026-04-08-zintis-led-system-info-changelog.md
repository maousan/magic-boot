# 2026-04-08 zintis-led system info changelog

## added
- New API endpoint: `POST /led/system/info`.
- New DTO: `LedSystemInfoResponse`.
- Service capability to parse system info payload into:
  - `payloadHex`
  - `payloadAscii`

## test updates
- Added service unit test for system info query success path.
- Updated `http/test-magic-plugin-zintis-led.http` with system info request example.
