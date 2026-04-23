# 2026-04-22 zintis-led netty client list changelog

## added
- Added active netty client list API:
  - `GET /led/netty/server/clients`

## dto
- Added `LedNettyClientListResponse`:
  - `running`
  - `totalClients`
  - `clients`
  - `message`

## service/handler
- `LedNettyServerHandler` now exposes active remote address listing.
- `LedNettyServerService` adds `listClients()`.
- Controller wired with OpenAPI documentation.

## test updates
- Updated `LedNettyServerServiceTest` with client-list behavior verification.
