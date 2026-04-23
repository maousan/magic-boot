# 2026-04-22 zintis-led netty active send changelog

## added
- Added Netty active-send capability for connected clients.
- New APIs:
  - `POST /led/netty/server/send` (send to one client)
  - `POST /led/netty/server/broadcast` (send to all clients)

## dto
- Added `LedNettySendRequest`.
- Added `LedNettyBroadcastRequest`.
- Added `LedNettySendResponse`.

## service/handler
- `LedNettyServerHandler` now keeps active writable channels and supports:
  - `sendTo(remoteAddress, data)`
  - `broadcast(data)`
- `LedNettyServerService` now supports payload parsing:
  - `ascii`
  - `hex` (supports `0x`, spaces, commas)
- Kept backward behavior: `start(0)` still allowed (random available port).

## tests
- Updated `LedNettyServerServiceTest` with payload parsing tests.
- Existing netty handler/store tests remain green.
