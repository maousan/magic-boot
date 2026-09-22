import client from './request'
import { sha256 } from 'js-sha256'
import type { ApiResponse, PageResult } from '@/types'

export interface AdminUserItem {
  id: string
  username: string
  name: string | null
  role: 'admin' | 'ops'
  /** 禁止登录：0未禁用，1已禁用 */
  isLogin: number
  createDate: string
}

export interface AdminUserListParams {
  page: number
  pageSize: number
  username?: string
  name?: string
}

export function listAdminUsers(params: AdminUserListParams): Promise<PageResult<AdminUserItem>> {
  return client.get<ApiResponse<PageResult<AdminUserItem>>>('/system/user/list', { params }).then((r) => r.data.data)
}

export interface SaveAdminUserParams {
  username: string
  name?: string
  /** sha256(明文) */
  password: string
  role: 'admin' | 'ops'
}

export function saveAdminUser(params: SaveAdminUserParams): Promise<void> {
  return client.post<ApiResponse<boolean>>('/system/user/save', {
    ...params,
    password: sha256(params.password),
  }).then(() => {})
}

export function updateAdminUser(params: { id: string; name?: string; role: 'admin' | 'ops' }): Promise<void> {
  return client.post<ApiResponse<boolean>>('/system/user/update', params).then(() => {})
}

export function resetAdminUserPassword(params: { id: string; password: string }): Promise<void> {
  return client.post<ApiResponse<boolean>>('/system/user/reset-password', {
    id: params.id,
    password: sha256(params.password),
  }).then(() => {})
}

export function toggleAdminUserLogin(params: { id: string; isLogin: 0 | 1 }): Promise<void> {
  return client.post<ApiResponse<boolean>>('/system/user/toggle-login', params).then(() => {})
}

export function deleteAdminUser(id: string): Promise<void> {
  return client.post<ApiResponse<boolean>>('/system/user/delete', { id }).then(() => {})
}
