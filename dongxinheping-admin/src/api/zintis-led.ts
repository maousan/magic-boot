import { pluginClient as client } from './request'
import type {
  ZintisLedControlRequest,
  ZintisLedControlResponse,
  ZintisLedQueryResponse,
  ZintisLedSystemInfoResponse,
  ZintisLedSignalStrengthResponse,
  ZintisLedNetworkResponse,
  ZintisLedTcpConfigRequest,
} from '@/types'

const BASE = '/plugin/zintis-led-plugin/api'

export function zintisControlOn(data: ZintisLedControlRequest): Promise<ZintisLedControlResponse> {
  return client.post(`${BASE}/control/on`, data).then((r) => r.data)
}

export function zintisControlOff(data: ZintisLedControlRequest): Promise<ZintisLedControlResponse> {
  return client.post(`${BASE}/control/off`, data).then((r) => r.data)
}

export function zintisControlPulse(data: ZintisLedControlRequest): Promise<ZintisLedControlResponse> {
  return client.post(`${BASE}/control/pulse`, data).then((r) => r.data)
}

export function zintisControlQuery(data: ZintisLedControlRequest): Promise<ZintisLedQueryResponse> {
  return client.post(`${BASE}/control/query`, data).then((r) => r.data)
}

export function zintisSystemInfo(data: ZintisLedControlRequest): Promise<ZintisLedSystemInfoResponse> {
  return client.post(`${BASE}/system/info`, data).then((r) => r.data)
}

export function zintisSignalStrength(data: ZintisLedControlRequest): Promise<ZintisLedSignalStrengthResponse> {
  return client.post(`${BASE}/system/signal-strength`, data).then((r) => r.data)
}

export function zintisNetwork(data: ZintisLedControlRequest): Promise<ZintisLedNetworkResponse> {
  return client.post(`${BASE}/system/network`, data).then((r) => r.data)
}

export function zintisTcpServerOpen(data: ZintisLedTcpConfigRequest): Promise<ZintisLedControlResponse> {
  return client.post(`${BASE}/tcp/server/open`, data).then((r) => r.data)
}

export function zintisTcpServerClose(data: ZintisLedTcpConfigRequest): Promise<ZintisLedControlResponse> {
  return client.post(`${BASE}/tcp/server/close`, data).then((r) => r.data)
}

export function zintisTcpClientOpen(data: ZintisLedTcpConfigRequest): Promise<ZintisLedControlResponse> {
  return client.post(`${BASE}/tcp/client/open`, data).then((r) => r.data)
}

export function zintisTcpClientClose(data: ZintisLedTcpConfigRequest): Promise<ZintisLedControlResponse> {
  return client.post(`${BASE}/tcp/client/close`, data).then((r) => r.data)
}
