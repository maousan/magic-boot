# zintis-led netty recv ascii conversion plan

## goal
- When Netty server receives a frame, automatically convert bytes to text characters for direct readability in logs.

## scope
- Update `LedNettyServerHandler` receive log output.
- Add byte-to-text conversion utility:
  - Keep printable ASCII (`0x20..0x7E`).
  - Replace non-printable bytes with `.`.
- Keep original hex output to avoid losing protocol-level observability.

## implementation
1. In `channelRead`, output both `hex` and `ascii`.
2. Add `toAsciiText(byte[])` in handler for unified conversion.
3. Add unit tests for conversion behavior.

## verification
- Run `mvn -Dtest=LedNettyServerHandlerTest,LedControlServiceTest test` under module `magic-plugin-zintis-led`.
- Run `mvn -pl magic-plugin-zintis-led -am -DskipTests package` from repo root.
- Replace `plugins/magic-plugin-zintis-led.jar` with the new build.
