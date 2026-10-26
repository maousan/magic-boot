import client from './request'
import type { ApiResponse } from '@/types'

export interface AccessLogRow {
  id: string
  requestPath: string
  requestMethod: string
  userId: string | null
  ipAddress: string | null
  executeTime: number | null
  state: string
  exceptionMessage: string | null
  createTime: string
}

export interface AccessLogDetail extends AccessLogRow {
  requestParams: string | null
}

export interface AccessLogQuery {
  pageNo: number
  pageSize: number
  keyword?: string
  state?: string
}

export function listAccessLogs(query: AccessLogQuery): Promise<{ list: AccessLogRow[]; total: number }> {
  return client
    .get<ApiResponse<{ list: AccessLogRow[]; total: number }>>('/system/access-log/list', { params: query })
    .then((r) => r.data.data)
}

export function getAccessLogDetail(id: string): Promise<AccessLogDetail> {
  return client
    .get<ApiResponse<AccessLogDetail>>('/system/access-log/detail', { params: { id } })
    .then((r) => r.data.data)
}

export function cleanAccessLogs(days: number): Promise<{ days: number; accessDeleted: number }> {
  return client
    .post<ApiResponse<{ days: number; accessDeleted: number }>>('/system/access-log/clean', { days })
    .then((r) => r.data.data)
}
