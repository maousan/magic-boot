# 2026-04-22 milestone: netty receive auto text conversion

## delivered
- Netty receive logs now include automatic text-character view (`ascii`) along with raw `hex`.
- Conversion utility and unit tests completed and merged into plugin module.

## acceptance
- Inbound frames produce readable receive logs in the format:
  - `hex=<...>, ascii=<...>`
- Non-printable bytes are safely represented as `.`.

## next
- Optional: expose recent receive `hex/ascii` pairs via API for UI-side diagnostics.
