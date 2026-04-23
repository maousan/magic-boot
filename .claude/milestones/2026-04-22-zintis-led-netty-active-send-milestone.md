# 2026-04-22 milestone: netty active send capability

## delivered
- Server can proactively send data to connected clients.
- Supports both targeted send and broadcast send.
- Supports `ascii` and `hex` payload input formats.

## acceptance
- API can send to specific `remoteAddress`.
- API can broadcast to all active clients.
- Send response reports target count/success/fail summary.

## next
- Optional: add endpoint to list current active remote addresses for easier targeting.
