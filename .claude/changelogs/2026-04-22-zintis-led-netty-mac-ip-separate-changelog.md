# 2026-04-22 zintis-led netty mac ip separate changelog

## changed
- Netty receive log now separates network fields:
  - `mac`
  - `ip`
- Parsing priority:
  1. length-prefixed payload format
  2. ASCII text regex fallback

## unchanged
- `hex` full-frame output remains.
- `payloadAscii` remains for readability.
- `crc` remains independent and keeps using last 2 bytes of frame.

## test updates
- Expanded `LedNettyServerHandlerTest`:
  - length-prefixed payload extraction for MAC/IP
  - ASCII fallback extraction for MAC/IP
