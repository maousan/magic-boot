# zintis-led local netty server plan

## goal
- Start local Netty server as LED upper computer.
- Default server port: `9834`.

## scope
- Add `LedNettyServerService` with:
  - auto start on plugin init
  - manual start/stop/status methods
- Add APIs:
  - `POST /led/netty/server/start`
  - `POST /led/netty/server/stop`
  - `GET /led/netty/server/status`
- Add Netty frame decoder and inbound handler for frame logging.
- Add test coverage.

## verification
- Run `mvn -pl magic-plugin-zintis-led package`.
- Copy built jar to `plugins` directory.
