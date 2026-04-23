# 2026-04-22 zintis-led netty hex prefix format changelog

## update
- Confirmed netty active-send `hex` payload parser supports prefixed token format:
  - `0x66 0x35 0xBA 0x3C 0x07`

## test
- Added explicit test assertion in `LedNettyServerServiceTest` for prefixed-space hex format.

## docs
- Updated `http/test-magic-plugin-zintis-led.http` with executable examples:
  - prefixed hex format example
  - plain hex format example
