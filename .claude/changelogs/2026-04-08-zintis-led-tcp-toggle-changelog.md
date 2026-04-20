# 2026-04-08 zintis-led tcp toggle changelog

## added
- TCP server control APIs:
  - `POST /led/tcp/server/open`
  - `POST /led/tcp/server/close`
- TCP client control APIs:
  - `POST /led/tcp/client/open`
  - `POST /led/tcp/client/close`

## protocol mapping
- `CMD_TCP_CONFIG (0xBF)` with sub command:
  - `0x32` open TCP server
  - `0x2E` close TCP server
  - `0x33` open TCP client
  - `0x3E` close TCP client

## test updates
- Added service tests for 4 TCP toggle operations.
- Added HTTP examples using:
  - `deviceIp=192.168.2.198`
  - `hostAddress=198`
