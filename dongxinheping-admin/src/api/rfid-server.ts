import client, { pluginClient } from './request'

export interface RfidServerStatus {
  running: boolean
  port: number
  activeConnections: number
}

export interface RfidDeviceInfo {
  deviceId: string
  remoteAddress: string
  connectedAt: number
  lastActiveAt: number
}

export interface RfidDeviceList {
  running: boolean
  devices: RfidDeviceInfo[]
}

export interface RfidCommandResult {
  success: boolean
  msgId?: string
  status?: string
  message?: string
}

export function getRfidStatus(): Promise<RfidServerStatus> {
  return pluginClient.get('/plugin/zintis-rfid-plugin/api/rfid/status').then((r) => r.data)
}

export function startRfidServer(port?: number): Promise<RfidServerStatus> {
  return pluginClient.post('/plugin/zintis-rfid-plugin/api/rfid/start', port ? { port } : {}).then((r) => r.data)
}

export function stopRfidServer(): Promise<RfidServerStatus> {
  return pluginClient.post('/plugin/zintis-rfid-plugin/api/rfid/stop').then((r) => r.data)
}

export function getRfidDevices(): Promise<RfidDeviceList> {
  return pluginClient.get('/plugin/zintis-rfid-plugin/api/rfid/devices').then((r) => r.data)
}

export function sendRfidCommand(data: {
  deviceId: string
  command: string
  params?: Record<string, unknown>
}): Promise<RfidCommandResult> {
  return pluginClient.post('/plugin/zintis-rfid-plugin/api/rfid/command', data).then((r) => r.data)
}

export interface RfidRecord {
  id: number
  device_id: string
  epc: string
  rssi: number
  read_time: string
}

export function getRfidRecords(params: {
  page: number
  pageSize: number
}): Promise<{ list: RfidRecord[]; total: number }> {
  return client.get('/rfid/records', { params }).then((r) => r.data.data)
}
