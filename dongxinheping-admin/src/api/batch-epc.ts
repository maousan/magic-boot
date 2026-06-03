import client from './request'
import type { BatchEpcBinding, PageResult, ApiResponse } from '@/types'

export function getBatchEpcList(params: {
  page: number
  pageSize: number
  status?: number
  keyword?: string
}): Promise<PageResult<BatchEpcBinding>> {
  return client
    .get<ApiResponse<PageResult<BatchEpcBinding>>>('/location/batch-epc/list', { params })
    .then((r) => r.data.data)
}

export function bindBatchEpc(data: { locationId: string; batchId: string; epc: string }): Promise<BatchEpcBinding> {
  return client.post<ApiResponse<BatchEpcBinding>>('/location/batch-epc/bind', data).then((r) => r.data.data)
}

export function unbindBatchEpc(data: { batchId?: string; epc?: string }): Promise<void> {
  return client.post('/location/batch-epc/unbind', data).then(() => {})
}

export function updateBatchEpc(data: { id: string; batchId: string; epc: string }): Promise<BatchEpcBinding> {
  return client.post<ApiResponse<BatchEpcBinding>>('/location/batch-epc/update', data).then((r) => r.data.data)
}

export function deleteBatchEpc(data: { id: string }): Promise<void> {
  return client.post('/location/batch-epc/delete', data).then(() => {})
}
