# 2026-04-22 zintis-led netty recv ascii changelog

## changed
- `LedNettyServerHandler` now logs inbound Netty frames with both:
  - `hex` raw bytes
  - `ascii` text view
- Added `toAsciiText(byte[])` conversion rule:
  - printable ASCII kept as-is
  - non-printable bytes converted to `.`

## test updates
- Added `LedNettyServerHandlerTest` to verify conversion behavior.
- Existing `LedControlServiceTest` remains compatible with current TCP payload behavior.

## impact
- Operators can directly read text-like payload content from receive logs without manual hex decoding.
- Hex logs remain unchanged for protocol troubleshooting.
