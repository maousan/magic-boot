import client from './request'
import type { ImportResult, WarehouseLocation, ApiResponse, PageResult } from '@/types'

export function getWarehouseLocations(params: {
  page?: number
  pageSize?: number
  warehouseCode?: string
  locationId?: string
}): Promise<PageResult<WarehouseLocation>> {
  return client
    .get<ApiResponse<PageResult<WarehouseLocation>>>('/location/warehouse-location/list', { params })
    .then((r) => r.data.data)
}

export function addWarehouseLocation(data: {
  warehouseCode: string
  locationId: string
}): Promise<string> {
  return client
    .post<ApiResponse<string>>('/location/warehouse-location/add', data)
    .then((r) => r.data.data)
}

export function updateWarehouseLocation(data: {
  id: string
  warehouseCode: string
  locationId: string
}): Promise<string> {
  return client
    .post<ApiResponse<string>>('/location/warehouse-location/update', data)
    .then((r) => r.data.data)
}

export function deleteWarehouseLocation(id: string): Promise<string> {
  return client
    .delete<ApiResponse<string>>('/location/warehouse-location/delete', { params: { id } })
    .then((r) => r.data.data)
}

export function importWarehouseLocations(file: File): Promise<ImportResult> {
  const form = new FormData()
  form.append('file', file)
  return client
    .post<ApiResponse<ImportResult>>('/location/warehouse-location/import', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    .then((r) => r.data.data)
}

export function syncLocationInventory(data: {
  warehouseCode: string
  locationId: string
}): Promise<{ success: boolean; savedCount: number; skippedCount: number; message?: string }> {
  return client
    .post<ApiResponse<{ success: boolean; savedCount: number; skippedCount: number; message?: string }>>(
      '/location/warehouse-location/sync-inventory',
      data,
    )
    .then((r) => r.data.data)
}

export function updateLocationArticle(data: {
  locationId: string
}): Promise<{ success: boolean; rowCount: number; message?: string }> {
  return client
    .post<ApiResponse<{ success: boolean; rowCount: number; message?: string }>>(
      '/location/warehouse-location/update-article',
      data,
    )
    .then((r) => r.data.data)
}
