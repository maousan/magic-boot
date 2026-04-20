# 2026-04-08 magic-plugin-zintis-led MVP Changelog

## Added
- New PF4J module `magic-plugin-zintis-led`.
- New plugin entry `ZintisLedPlugin`.
- LED protocol codec and CRC validator.
- TCP client manager with connection lifecycle handling.
- LED control service and REST APIs (on/off/pulse/query/health).
- Unit and integration tests.
- HTTP test script for manual API verification.

## Notes
- This release is MVP only and focuses on single-device serial control.