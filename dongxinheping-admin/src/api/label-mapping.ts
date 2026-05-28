import client from './request'
import type { LocationLabelBinding, PageResult, ApiResponse } from '@/types'

export function getLabelMappingList(params: {
  page: number
  pageSize: number
  locationCode?: string
  labelCode?: string
}): Promise<PageResult<LocationLabelBinding>> {
  return client.get<ApiResponse<PageResult<LocationLabelBinding>>>('/location/label-mapping/list', { params }).then((r) => r.data.data)
}

export function createLabelMapping(data: { locationCode: string; labelCode: string }): Promise<void> {
  return client.post('/location/label-mapping', data).then(() => {})
}

export function deleteLabelMapping(id: string): Promise<void> {
  return client.delete('/location/label-mapping', { params: { id } }).then(() => {})
}

export function lightOnLabel(data: { labelCode: string; color: string; duration: string }): Promise<{ success: boolean }> {
  return client.post<ApiResponse<{ success: boolean }>>('/location/label/light-on', data).then((r) => r.data.data)
}

export function lightOffLabel(data: { labelCode: string }): Promise<{ success: boolean }> {
  return client.post<ApiResponse<{ success: boolean }>>('/location/label/light-off', data).then((r) => r.data.data)
}
