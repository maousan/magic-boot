# zintis-led netty report json persistence plan

## goal
- Persist Netty receive reports into a local JSON file automatically.

## scope
- On each inbound Netty frame, persist structured fields:
  - timestamp
  - remoteAddress
  - hex
  - payloadAscii
  - mac
  - ip
  - crc
- Keep existing receive logs unchanged.

## design
1. Add a dedicated local store class under netty transport package.
2. Store data in `data/zintis-led/netty-recv-report.json`.
3. File format: JSON array.
4. Use file lock to avoid concurrent write corruption.
5. Keep a bounded history size to prevent unlimited growth.

## verification
- Unit test report persistence and cap behavior.
- Run `mvn "-Dtest=LedNettyServerHandlerTest,LedNettyMessageReportStoreTest" test` in module.
- Package plugin and replace plugin jar under `plugins`.
