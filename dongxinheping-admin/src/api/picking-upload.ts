import client from './request'
import type { PickingUpload, PickingUploadDetail, PageResult, ApiResponse } from '@/types'

export function getPickingUploadList(params: {
  page: number
  pageSize: number
  waveNo?: string
  userId?: string
  userName?: string
}): Promise<PageResult<PickingUpload>> {
  return client
    .get<ApiResponse<PageResult<PickingUpload>>>('/picking-upload/list', { params })
    .then((r) => r.data.data)
}

export function getPickingUploadDetail(id: string): Promise<{
  master: PickingUpload
  details: PickingUploadDetail[]
}> {
  return client
    .get<ApiResponse<{ master: PickingUpload; details: PickingUploadDetail[] }>>('/picking-upload/detail', { params: { id } })
    .then((r) => r.data.data)
}

export function addPickingUpload(data: {
  waveNo: string
  userId: string
  userName?: string
}): Promise<string> {
  return client
    .post<ApiResponse<string>>('/picking-upload/add', data)
    .then((r) => r.data.data)
}

export function updatePickingUpload(data: {
  id: string
  waveNo?: string
  userId?: string
  userName?: string
}): Promise<string> {
  return client
    .post<ApiResponse<string>>('/picking-upload/update', data)
    .then((r) => r.data.data)
}

export function deletePickingUpload(id: string): Promise<string> {
  return client
    .post<ApiResponse<string>>('/picking-upload/delete', { id })
    .then((r) => r.data.data)
}

export function addPickingUploadDetail(data: {
  billId: string
  materialCode: string
  batchNo: string
  locationCode: string
  planQuantity: number
  actualQuantity?: number
  status?: number
}): Promise<string> {
  return client
    .post<ApiResponse<string>>('/picking-upload/detail/add', data)
    .then((r) => r.data.data)
}

export function updatePickingUploadDetail(data: {
  id: string
  materialCode?: string
  batchNo?: string
  locationCode?: string
  planQuantity?: number
  actualQuantity?: number
  status?: number
}): Promise<string> {
  return client
    .post<ApiResponse<string>>('/picking-upload/detail/update', data)
    .then((r) => r.data.data)
}

export function deletePickingUploadDetail(id: string): Promise<string> {
  return client
    .post<ApiResponse<string>>('/picking-upload/detail/delete', { id })
    .then((r) => r.data.data)
}
