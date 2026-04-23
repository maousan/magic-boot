# 2026-04-22 zintis-led netty report json changelog

## added
- Added local JSON persistence for Netty receive reports.
- New store class: `LedNettyMessageReportStore`.
- Default output file: `data/zintis-led/netty-recv-report.json`.

## report fields
- `timestamp`
- `remoteAddress`
- `hex`
- `payloadAscii`
- `mac`
- `ip`
- `crc`

## behavior
- Each receive event is appended to JSON array storage.
- File writes are lock-protected for concurrent safety.
- History is bounded (default max 1000 records).

## test updates
- Added `LedNettyMessageReportStoreTest`.
- Verified with `LedNettyServerHandlerTest`.
