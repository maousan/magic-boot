import client from './request'
import type { LedColor, PageResult, ApiResponse } from '@/types'

export function getLedColorList(params: {
  page: number
  pageSize: number
  code?: string
  color?: string
}): Promise<PageResult<LedColor>> {
  return client.get<ApiResponse<PageResult<LedColor>>>('/location/led-color/list', { params }).then((r) => r.data.data)
}

export function createLedColor(data: {
  code: string
  color: string
  remark?: string
}): Promise<string> {
  return client.post<ApiResponse<string>>('/location/led-color', data).then((r) => r.data.data)
}

export function updateLedColor(data: {
  id: number
  code?: string
  color?: string
  remark?: string
}): Promise<string> {
  return client.put<ApiResponse<string>>('/location/led-color', data).then((r) => r.data.data)
}

export function deleteLedColor(id: number): Promise<string> {
  return client.delete<ApiResponse<string>>('/location/led-color', { params: { id } }).then((r) => r.data.data)
}
