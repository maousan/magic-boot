# zintis-led netty array numeric payload plan

## goal
- Add array numeric payload support for netty active-send APIs.

## scope
- `POST /led/netty/server/send`
- `POST /led/netty/server/broadcast`

## format
- New optional field: `payloadArray`
- Example: `[102,53,186,60,7]`
- Value range: `0..255`

## compatibility
- Keep existing string payload behavior:
  - `payloadFormat=ascii`
  - `payloadFormat=hex`
- If `payloadArray` provided, it has priority over string payload.

## verification
- Extend service tests to cover array payload parse and invalid range.
- Update http examples.
