import client from './request'
import type { ApiResponse, LicenseStatusView } from '@/types'

export function getLicenseStatus(): Promise<LicenseStatusView> {
  return client.get<ApiResponse<LicenseStatusView>>('/system/license/status').then(r => r.data.data)
}

export function importLicense(formData: FormData): Promise<LicenseStatusView> {
  return client.post<ApiResponse<LicenseStatusView>>('/system/license/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }).then(r => r.data.data)
}

/**
 * 签发并下载 .lic 文件（仅办公实例可用；现场未配置签发私钥时接口 404）。
 * blob 响应绕过统一 code 拦截；调用方需识别 JSON 错误响应。
 */
export async function issueLicense(formData: FormData): Promise<{ blob: Blob; filename: string }> {
  const resp = await client.post('/system/license/issue/download', formData, { responseType: 'blob' })
  const disposition = String(resp.headers?.['content-disposition'] ?? '')
  let filename = 'license.lic'
  const match = disposition.match(/filename\*=UTF-8''([^;]+)/)
  if (match && match[1]) {
    filename = decodeURIComponent(match[1])
  }
  return { blob: resp.data as Blob, filename }
}
