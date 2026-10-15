import client from './request'
import type { ApiResponse } from '@/types'

export interface RoleItem {
  id: string
  name: string
  code: string
  userCount: number
  createDate: string
}

export function listRoles(): Promise<RoleItem[]> {
  return client.get<ApiResponse<RoleItem[]>>('/system/role/list').then((r) => r.data.data)
}

export function createRole(data: { name: string; code: string }): Promise<void> {
  return client.post('/system/role/create', data).then(() => {})
}

export function updateRole(data: { id: string; name: string }): Promise<void> {
  return client.post('/system/role/update', data).then(() => {})
}

export function deleteRole(id: string): Promise<void> {
  return client.post('/system/role/delete', { id }).then(() => {})
}

export interface RoleMember {
  id: string
  username: string
  name: string | null
}

export function listRoleMembers(roleId: string): Promise<RoleMember[]> {
  return client
    .get<ApiResponse<RoleMember[]>>('/system/role/members', { params: { roleId } })
    .then((r) => r.data.data)
}

export function saveRoleMembers(roleId: string, userIds: string[]): Promise<void> {
  return client.post('/system/role/save-members', { roleId, userIds }).then(() => {})
}
