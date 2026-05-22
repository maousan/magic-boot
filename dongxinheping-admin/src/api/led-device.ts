import client from './request'
import type { LedDevice, LedControlCommand, LedControlResult, PageResult, ApiResponse } from '@/types'

export function getLedDeviceList(params: {
  page: number
  pageSize: number
  macAddress?: string
  ip?: string
  remark?: string
}): Promise<PageResult<LedDevice>> {
  return client.get<ApiResponse<PageResult<LedDevice>>>('/location/led-device/list', { params }).then((r) => r.data.data)
}

export function createLedDevice(data: {
  macAddress: string
  ip: string
  remark?: string
}): Promise<string> {
  return client.post<ApiResponse<string>>('/location/led-device', data).then((r) => r.data.data)
}

export function updateLedDevice(data: {
  macAddress: string
  ip?: string
  remark?: string
}): Promise<string> {
  return client.put<ApiResponse<string>>('/location/led-device', data).then((r) => r.data.data)
}

export function deleteLedDevice(macAddress: string): Promise<string> {
  return client.delete<ApiResponse<string>>('/location/led-device', { params: { macAddress } }).then((r) => r.data.data)
}

export function controlLedDevice(cmd: LedControlCommand): Promise<LedControlResult> {
  return client.post<ApiResponse<LedControlResult>>('/location/led-device-control', cmd).then((r) => r.data.data)
}
