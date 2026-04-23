# zintis-led netty active client list plan

## goal
- Provide API to list active netty client remote addresses.

## scope
- Expose active client addresses from netty handler.
- Add service method and DTO for client list response.
- Add controller endpoint for querying active clients.

## implementation
1. Add `listActiveRemoteAddresses()` in `LedNettyServerHandler`.
2. Add DTO `LedNettyClientListResponse`.
3. Add `listClients()` in `LedNettyServerService`.
4. Add API `GET /led/netty/server/clients`.

## verification
- Compile and run focused tests for netty service/handler.
- Package plugin and replace plugin jar in `plugins`.
