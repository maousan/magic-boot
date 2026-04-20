# magic-plugin-zintis-led MVP Implementation Plan

## Summary
- Goal: implement deployable PF4J plugin `magic-plugin-zintis-led`.
- Scope: protocol codec + TCP client + control APIs + basic logging.

## Key Changes
- Add independent module `magic-plugin-zintis-led` and register in root `pom.xml`.
- Plugin entry and metadata: `ZintisLedPlugin`, `plugin.properties`, `Manifest.json`.
- Protocol and transport: `LedCommandConstants`, `Crc16Modbus`, `LedProtocolCodec`, `LedTcpClientManager`.
- Service and API: `LedControlService`, `LedDeviceController`.
- DTOs and error mapping: `LedControlRequest/Response`, `LedQueryResponse`.

## Test Plan
- Unit tests: `Crc16ModbusTest`, `LedProtocolCodecTest`, `LedControlServiceTest`.
- Integration tests: `LedTcpClientManagerIntegrationTest`, `ZintisLedPluginLifecycleTest`.
- HTTP test: `http/test-magic-plugin-zintis-led.http`.

## Assumptions
- Excluded in MVP: frontend, WebSocket, TCP server mode, batch control, persistence, monitoring dashboard.