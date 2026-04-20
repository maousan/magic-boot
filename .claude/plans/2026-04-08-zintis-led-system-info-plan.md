# zintis-led system info feature plan

## goal
- Add a dedicated API to query device system information.

## scope
- Add service method `querySystemInfo`.
- Add endpoint `POST /led/system/info`.
- Add response DTO with hex and ascii payload fields.
- Add unit test coverage and update `.http` script.

## verification
- Run `mvn -pl magic-plugin-zintis-led test`.
