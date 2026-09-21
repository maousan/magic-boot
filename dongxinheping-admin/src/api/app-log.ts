import client from './request'
import type { AppDeviceLog, PageResult, ApiResponse } from '@/types'

const DOWNLOAD_BASE = '/plugin/dongxinheping-plugin/api/applog/download'

export function getAppLogList(params: {
  pageNo: number
  pageSize: number
  deviceId?: string
}): Promise<PageResult<AppDeviceLog>> {
  return client
    .get<ApiResponse<PageResult<AppDeviceLog>>>('/app/log/list', { params })
    .then(r => r.data.data)
}

export function deleteAppLog(id: number): Promise<void> {
  return client.delete<ApiResponse<null>>(`/app/log/delete/${id}`).then(() => {})
}

// 浏览器直接打开下载（同源，zip 由插件控制器流出，magic-api 无法干净输出二进制）
export function appLogDownloadUrl(row: Pick<AppDeviceLog, 'deviceId' | 'fileName'>): string {
  return `${DOWNLOAD_BASE}?deviceId=${encodeURIComponent(row.deviceId)}&fileName=${encodeURIComponent(row.fileName)}`
}
