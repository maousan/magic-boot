# 2026-04-22 milestone: netty mac/ip separated output

## delivered
- Netty receive logs can now show MAC and IP separately.
- Compatible with both length-prefixed payload and plain-text payload styles.

## acceptance
- For sample frame `66AB97110D...9078`, logs can output:
  - `mac=3A:69:7A:08:D0:A5`
  - `ip=192.168.2.102`
  - `crc=9078`

## next
- Optional: expose parsed `mac/ip/crc` through status query API if needed.
