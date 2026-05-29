import client from './request'
import type { AppVersion, PageResult, ApiResponse } from '@/types'

export function getAppVersionList(params: {
  page: number
  pageSize: number
  keyword?: string
}): Promise<PageResult<AppVersion>> {
  return client.get<ApiResponse<PageResult<AppVersion>>>('/app/version/list', { params }).then(r => r.data.data)
}

export function createAppVersion(formData: FormData): Promise<void> {
  return client.post('/app/version/save', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }).then(() => {})
}

export function updateAppVersion(formData: FormData): Promise<void> {
  return client.post('/app/version/update', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }).then(() => {})
}

export function deleteAppVersion(id: string): Promise<void> {
  return client.delete<ApiResponse<null>>(`/app/version/delete/${id}`).then(() => {})
}
