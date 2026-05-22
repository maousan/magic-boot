import client from './request'
import type { UserLightColor, PageResult, ApiResponse } from '@/types'

export function getUserLightColorList(params: {
  page: number
  pageSize: number
  userId?: string
}): Promise<PageResult<UserLightColor>> {
  return client
    .get<ApiResponse<PageResult<UserLightColor>>>('/user-light-color/list', { params })
    .then((r) => r.data.data)
}

export function addUserLightColor(data: {
  userId: string
  color: string
  enabled?: number
  remark?: string
}): Promise<string> {
  return client
    .post<ApiResponse<string>>('/user-light-color/add', data)
    .then((r) => r.data.data)
}

export function updateUserLightColor(data: {
  id: number
  userId?: string
  color?: string
  enabled?: number
  remark?: string
}): Promise<string> {
  return client
    .post<ApiResponse<string>>('/user-light-color/update', data)
    .then((r) => r.data.data)
}

export function deleteUserLightColor(id: number): Promise<string> {
  return client
    .post<ApiResponse<string>>('/user-light-color/delete', { id })
    .then((r) => r.data.data)
}
