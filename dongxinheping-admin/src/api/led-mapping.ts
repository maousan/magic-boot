import client from './request'
import type { LocationLedBinding, PageResult, ApiResponse } from '@/types'

export function getLedMappingList(params: {
  page: number
  pageSize: number
  locationCode?: string
  ledId?: string
  color?: string
}): Promise<PageResult<LocationLedBinding>> {
  return client.get<ApiResponse<PageResult<LocationLedBinding>>>('/location/led-mapping/list', { params }).then((r) => r.data.data)
}

export function createLedMapping(data: { lotNo: string; ledId: string; color: string }): Promise<void> {
  return client.post('/location/led-mapping', data).then(() => {})
}

export function deleteLedMapping(id: string): Promise<void> {
  return client.delete('/location/led-mapping', { params: { id } }).then(() => {})
}
