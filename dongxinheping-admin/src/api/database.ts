import client from './request'
import type { ApiResponse } from '@/types'

export interface ColumnInfo {
  name: string
  type: string
  pk: number
  notnull: number
  dfltValue: string | null
}

export interface TableMeta {
  name: string
  type: string
  rowCount: number
  columns: ColumnInfo[]
}

export interface QueryResponse {
  list: Record<string, unknown>[]
  rowCount: number
  hasMore: boolean
  duration_ms: number
}

export interface ExecuteResponse {
  preview?: boolean
  affected?: number
  message?: string
  duration_ms?: number
}

export function getTables(): Promise<TableMeta[]> {
  return client.get<ApiResponse<TableMeta[]>>('/database/tables').then((r) => r.data.data)
}

export function executeQuery(sql: string): Promise<QueryResponse> {
  return client.post<ApiResponse<QueryResponse | Record<string, unknown>[]>>('/database/query', { sql }).then((r) => {
    const data = r.data.data
    if (Array.isArray(data)) {
      return {
        list: data,
        rowCount: data.length,
        hasMore: false,
        duration_ms: 0,
      }
    }
    return {
      list: Array.isArray(data?.list) ? data.list : [],
      rowCount: typeof data?.rowCount === 'number' ? data.rowCount : Array.isArray(data?.list) ? data.list.length : 0,
      hasMore: Boolean(data?.hasMore),
      duration_ms: typeof data?.duration_ms === 'number' ? data.duration_ms : 0,
    }
  })
}

export function executeWrite(sql: string, confirmed: boolean): Promise<ExecuteResponse> {
  return client.post<ApiResponse<ExecuteResponse>>('/database/execute', { sql, confirmed }).then((r) => r.data.data)
}
