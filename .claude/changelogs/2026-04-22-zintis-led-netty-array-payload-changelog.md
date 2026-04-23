# 2026-04-22 zintis-led netty array payload changelog

## added
- Netty active-send now supports numeric array payload format.
- New request field: `payloadArray` (List<Integer>, range 0..255).

## behavior
- Parsing priority:
  1. `payloadArray` (if provided and non-empty)
  2. `payload + payloadFormat` (existing ascii/hex behavior)

## examples
- `[102,53,186,60,7]` -> `0x66 0x35 0xBA 0x3C 0x07`

## tests
- Extended `LedNettyServerServiceTest`:
  - array format parse success
  - invalid array range/null validation
  - compatibility with existing hex/ascii parse
