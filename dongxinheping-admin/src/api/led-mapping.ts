import client from './request'
import type { LocationLedBinding, PageResult, ApiResponse, ImportResult } from '@/types'

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

export function importLedMappings(file: File): Promise<ImportResult> {
  const form = new FormData()
  form.append('file', file)
  return client
    .post<ApiResponse<ImportResult>>('/location/led-mapping/import', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    .then((r) => r.data.data)
}
