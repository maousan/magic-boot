import client from './request'
import type { ApiResponse } from '@/types'

export interface AimsConfig {
  aimsHost: string
  stationCode: string
  mixColor: string
  controlDispatchAsync: string
}

export function getAimsConfig(): Promise<AimsConfig> {
  return client
    .get<ApiResponse<AimsConfig>>('/aims-config/get')
    .then((r) => r.data.data)
}

export function updateAimsConfig(data: Partial<AimsConfig>): Promise<string> {
  return client
    .post<ApiResponse<string>>('/aims-config/update', data)
    .then((r) => r.data.data)
}

export function testAimsConnection(): Promise<string> {
  return client
    .post<ApiResponse<string>>('/aims-config/test')
    .then((r) => r.data.message)
}
