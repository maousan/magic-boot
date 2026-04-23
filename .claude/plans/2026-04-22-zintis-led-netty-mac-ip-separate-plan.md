# zintis-led netty mac ip split plan

## goal
- Split MAC and IP into separate fields when Netty server receives frames.

## scope
- Parse payload and extract `mac` and `ip` independently.
- Keep existing `hex` and `payloadAscii` output.
- Keep CRC output as dedicated field.

## strategy
1. Extract payload from frame body (exclude header and CRC).
2. Prefer length-prefixed payload parsing (`macLen`, `ipLen`).
3. Fallback to regex parsing in ASCII payload text.
4. Output log fields: `hex`, `payloadAscii`, `mac`, `ip`, `crc`.

## verification
- Run `mvn "-Dtest=LedNettyServerHandlerTest" test` in module `magic-plugin-zintis-led`.
- Package and replace plugin jar in `plugins` directory.
