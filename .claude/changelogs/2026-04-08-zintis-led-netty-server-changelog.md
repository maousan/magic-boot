# 2026-04-08 zintis-led netty server changelog

## added
- Local Netty server feature for upper-computer mode.
- Default listen port set to `9834` on plugin startup.
- New APIs:
  - `POST /led/netty/server/start`
  - `POST /led/netty/server/stop`
  - `GET /led/netty/server/status`
- Netty transport classes:
  - `LedNettyFrameDecoder`
  - `LedNettyServerHandler`
  - `LedNettyServerService`

## test updates
- Added `LedNettyServerServiceTest`.
- Updated HTTP test file with netty server operation examples.
