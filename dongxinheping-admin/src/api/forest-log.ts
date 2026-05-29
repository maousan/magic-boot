import client from './request'
import type { ApiResponse } from '@/types'

export interface ForestLogConfig {
  logEnabled: boolean
  logRequest: boolean
  logResponseStatus: boolean
  logResponseHeaders: boolean
  logResponseContent: boolean
}

export function getForestLogConfig(): Promise<ForestLogConfig> {
  return client
    .get<ApiResponse<ForestLogConfig>>('/forest-log/get')
    .then((r) => r.data.data)
}

export function updateForestLogConfig(data: Partial<ForestLogConfig>): Promise<string> {
  return client
    .post<ApiResponse<string>>('/forest-log/update', data)
    .then((r) => r.data.data)
}
