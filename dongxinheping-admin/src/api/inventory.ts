import client from './request'
import type { InventoryItem, PageResult, ApiResponse } from '@/types'

export interface InventoryQueryParams {
  page: number
  pageSize: number
  warehouseId?: string
  locationId?: string
  sku?: string
  lotAtt09?: string
}

export function getInventoryList(params: InventoryQueryParams): Promise<PageResult<InventoryItem>> {
  return client.get<ApiResponse<PageResult<InventoryItem>>>('/location/inventory/list', { params }).then((r) => r.data.data)
}

export function createInventory(data: {
  warehouseId: string
  locationId: string
  sku: string
  lotAtt09?: string
  qty?: number
}): Promise<string> {
  return client.post<ApiResponse<string>>('/location/inventory/add', data).then((r) => r.data.data)
}

export function deleteInventory(params: {
  warehouseId: string
  locationId: string
  lotAtt09: string
}): Promise<string> {
  return client.delete<ApiResponse<string>>('/location/inventory/delete', { params }).then((r) => r.data.data)
}

export function clearInventory(): Promise<string> {
  return client.delete<ApiResponse<string>>('/location/inventory/clear', { params: { confirm: 'CONFIRM' } }).then((r) => r.data.data)
}
