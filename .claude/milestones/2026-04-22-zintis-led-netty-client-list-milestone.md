# 2026-04-22 milestone: netty active client list API

## delivered
- Added API to query current active netty client remote addresses.
- Works with existing active-send feature for easier target selection.

## acceptance
- `GET /led/netty/server/clients` returns:
  - server running status
  - total active client count
  - remote address list

## next
- Optional: expose connection duration and last seen timestamp per client.
