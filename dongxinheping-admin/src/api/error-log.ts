import client from './request'
import type { ApiResponse } from '@/types'

export interface ErrorLogRow {
  id: string
  requestPath: string
  requestMethod: string
  userId: string | null
  ipAddress: string | null
  exceptionClass: string | null
  exceptionMessage: string | null
  createTime: string
}

export interface ErrorLogDetail extends ErrorLogRow {
  requestParams: string | null
  requestHeaders: string | null
  stackTrace: string | null
}

export interface ErrorLogQuery {
  pageNo: number
  pageSize: number
  keyword?: string
}

export function listErrorLogs(query: ErrorLogQuery): Promise<{ list: ErrorLogRow[]; total: number }> {
  return client
    .get<ApiResponse<{ list: ErrorLogRow[]; total: number }>>('/system/error-log/list', { params: query })
    .then((r) => r.data.data)
}

export function getErrorLogDetail(id: string): Promise<ErrorLogDetail> {
  return client
    .get<ApiResponse<ErrorLogDetail>>('/system/error-log/detail', { params: { id } })
    .then((r) => r.data.data)
}

export function cleanErrorLogs(days: number): Promise<{ days: number; errorDeleted: number }> {
  return client
    .post<ApiResponse<{ days: number; errorDeleted: number }>>('/system/error-log/clean', { days })
    .then((r) => r.data.data)
}
