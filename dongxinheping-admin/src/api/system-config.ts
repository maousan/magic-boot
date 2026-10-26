import client from './request'
import type { ApiResponse } from '@/types'

export interface SystemConfigRow {
  id: string
  configureKey: string
  configureValue: string
  configureName: string | null
  remark: string | null
  updateDate: string | null
}

export function listSystemConfigs(): Promise<SystemConfigRow[]> {
  return client
    .get<ApiResponse<SystemConfigRow[]>>('/system/configure/list')
    .then((r) => r.data.data ?? [])
}

export function saveSystemConfig(data: { id: string; configureValue: string }): Promise<string> {
  return client
    .post<ApiResponse<string>>('/system/configure/save', data)
    .then((r) => r.data.data)
}
