# zintis-led tcp toggle feature plan

## goal
- Add TCP server/client open-close control APIs.

## scope
- Add `LedTcpConfigRequest` DTO.
- Add service methods:
  - `openTcpServer`
  - `closeTcpServer`
  - `openTcpClient`
  - `closeTcpClient`
- Add controller endpoints:
  - `POST /led/tcp/server/open`
  - `POST /led/tcp/server/close`
  - `POST /led/tcp/client/open`
  - `POST /led/tcp/client/close`
- Use protocol command mapping from document:
  - `0xBF + 0x32` open TCP server
  - `0xBF + 0x2E` close TCP server
  - `0xBF + 0x33` open TCP client
  - `0xBF + 0x3E` close TCP client

## verification
- Run `mvn -pl magic-plugin-zintis-led package`
- Copy built jar to `plugins` directory.
