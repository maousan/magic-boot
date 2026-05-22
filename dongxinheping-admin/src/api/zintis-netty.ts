import { pluginClient as client } from './request'
import type {
  ZintisNettyServerStatus,
  ZintisNettyClientList,
  ZintisNettySendRequest,
  ZintisNettyBroadcastRequest,
  ZintisNettySendResponse,
} from '@/types'

const BASE = '/plugin/zintis-led-plugin/api'

export function zintisNettyStart(port?: number): Promise<ZintisNettyServerStatus> {
  return client.post(`${BASE}/netty/server/start`, port ? { port } : {}).then((r) => r.data)
}

export function zintisNettyStop(): Promise<ZintisNettyServerStatus> {
  return client.post(`${BASE}/netty/server/stop`).then((r) => r.data)
}

export function zintisNettyStatus(): Promise<ZintisNettyServerStatus> {
  return client.get(`${BASE}/netty/server/status`).then((r) => r.data)
}

export function zintisNettyClients(): Promise<ZintisNettyClientList> {
  return client.get(`${BASE}/netty/server/clients`).then((r) => r.data)
}

export function zintisNettySend(data: ZintisNettySendRequest): Promise<ZintisNettySendResponse> {
  return client.post(`${BASE}/netty/server/send`, data).then((r) => r.data)
}

export function zintisNettyBroadcast(data: ZintisNettyBroadcastRequest): Promise<ZintisNettySendResponse> {
  return client.post(`${BASE}/netty/server/broadcast`, data).then((r) => r.data)
}
