import client from './request'
import type { ApiResponse } from '@/types'

export interface MenuConfigView {
  role: string
  menus: string[] | null
  /** 按钮权限点清单；null=未配置（视为全量） */
  buttons: string[] | null
  updateBy: string | null
  updateTime: string | null
}

export function getMenuConfig(roleCode: string): Promise<MenuConfigView> {
  return client
    .get<ApiResponse<MenuConfigView>>('/system/menu-config/detail', { params: { roleCode } })
    .then((r) => r.data.data)
}

export function saveMenuConfig(roleCode: string, menus: string[], buttons: string[]): Promise<void> {
  return client.post('/system/menu-config/save', { roleCode, menus, buttons }).then(() => {})
}
