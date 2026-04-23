# zintis-led netty active send plan

## goal
- Add active-send capability from server to connected netty clients.

## scope
- Keep connected client channels for write operations.
- Add service methods:
  - send to specific client by remote address
  - broadcast to all connected clients
- Add controller APIs for active send.
- Support payload input as `hex` or `ascii`.
- Return send result summary.

## implementation
1. Extend `LedNettyServerHandler` to track active channels and expose send methods.
2. Add request/response DTOs for send API.
3. Add `LedNettyServerService` wrappers for send operations.
4. Add controller endpoints under `/led/netty/server/send` and `/led/netty/server/broadcast`.
5. Add unit tests for payload parse and send path.

## verification
- Run module tests for netty send and existing netty handler behavior.
- Package plugin and replace plugin jar in `plugins` directory.
