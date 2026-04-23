# 2026-04-22 milestone: netty receive report local json persistence

## delivered
- Netty receive data is now persisted locally in JSON format.
- Receive report includes protocol and parsed network fields for audit/diagnostics.

## acceptance
- On each inbound frame, plugin writes/updates:
  - `data/zintis-led/netty-recv-report.json`
- Records include: timestamp, source, full frame hex, payload text, mac, ip, crc.

## next
- Optional: add rolling policy by file size/date if report volume grows.
