import { pluginClient as client } from './request'
import type {
  ZintisLanScanRequest,
  ZintisLanScanResponse,
} from '@/types'

const BASE = '/plugin/zintis-led-plugin/api'

export function zintisLanScan(data: ZintisLanScanRequest): Promise<ZintisLanScanResponse> {
  return client.post(`${BASE}/lan/scan`, data).then((r) => r.data)
}
