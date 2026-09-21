// LED 颜色映射
export interface LedColor {
  id: number
  code: string
  color: string
  remark: string
}

// LED 设备
export interface LedDevice {
  macAddress: string
  ip: string
  remark: string
}

// 库位-LED 绑定
export interface LocationLedBinding {
  id: string
  locationCode: string
  ledId: string
  color: 'RED' | 'YELLOW' | 'GREEN'
  status: number
}

// 库位-标签绑定
export interface LocationLabelBinding {
  id: string
  locationCode: string
  labelCode: string
  bindTime: string
}

// 批次 EPC 绑定
export interface BatchEpcBinding {
  id: string
  batchId: string
  epc: string
  status: number
  bindTime: string
  unbindTime?: string
}

// 库位记录
export interface WarehouseLocation {
  id: string
  warehouseCode: string
  locationId: string
  createTime: string
  updateTime: string
}

// 库位导入结果
export interface ImportResult {
  totalRows: number
  successCount: number
  failCount: number
  errors: Array<{ rowNo: number; message: string }>
}

// LED 控制命令
export interface LedControlCommand {
  mode: 'client'
  ledId: string
  command:
    | 'ON'
    | 'OFF'
    | 'CONTROL_ON'
    | 'CONTROL_OFF'
    | 'CONTROL_PULSE'
    | 'QUERY'
    | 'SYSTEM_INFO'
    | 'SIGNAL_STRENGTH'
    | 'NETWORK'
    | 'TCP_SERVER_OPEN'
    | 'TCP_SERVER_CLOSE'
    | 'TCP_CLIENT_OPEN'
    | 'TCP_CLIENT_CLOSE'
    | 'OTA_UPDATE'
  port?: 'ALL' | 'RED' | 'YELLOW' | 'GREEN'
  dataCommand?: number
  targetIp?: string
  targetPort?: number
  version?: string
  timeoutMs?: number
  waitResponse?: boolean
}

// LED 控制响应
export interface LedControlResult {
  ledId: string
  deviceIp: string
  remoteAddress?: string
  command: string
  port?: string
  timeoutMs: number
  success?: boolean
  payloadAscii?: string
  payloadHex?: string
  rawRequestHex?: string
  rawResponseHex?: string
  executedCount?: number
  executed?: unknown
  failed?: unknown
}

// 库存数据
export interface InventoryItem {
  warehouseId: string
  locationId: string
  sku: string
  lotAtt09: string
  qty: number
  qtyAllocated: number
  qtyPa: number
  editTime: string
  updateTime: string
}

// 通用分页响应
export interface PageResult<T> {
  list: T[]
  total: number
}

// 通用 API 响应
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

// ========== Zintis LED ==========

// Zintis LED 设备控制请求
export interface ZintisLedControlRequest {
  deviceIp: string
  devicePort?: number
  hostAddress: number
  dataCommand: number
  timeoutMs?: number
}

// Zintis LED 控制响应
export interface ZintisLedControlResponse {
  success: boolean
  message: string
  errorCode: string | null
  hostAddress: number
  controlCommand: string
  dataCommand: string
  responseControlCommand: string
  responseDataCommand: string
  rawRequestHex: string
  rawResponseHex: string
}

// Zintis LED 查询响应
export interface ZintisLedQueryResponse {
  success: boolean
  message: string
  errorCode: string | null
  hostAddress: number
  requestDataCommand: string
  responseControlCommand: string
  responseDataCommand: string
  payloadHex: string
  rawRequestHex: string
  rawResponseHex: string
}

// Zintis LED 系统信息响应
export interface ZintisLedSystemInfoResponse extends ZintisLedQueryResponse {
  payloadAscii: string
}

// Zintis LED 信号强度响应
export interface ZintisLedSignalStrengthResponse extends ZintisLedSystemInfoResponse {
  signalStrengthDbm: number
}

// Zintis LED 网络信息响应
export interface ZintisLedNetworkResponse extends ZintisLedSystemInfoResponse {
  systemIp: string
  systemMac: string
}

// Zintis LED TCP 配置请求
export interface ZintisLedTcpConfigRequest extends ZintisLedControlRequest {
  targetIp?: string
  targetPort?: number
}

export interface ZintisLedOtaUpdateRequest {
  deviceIp: string
  devicePort?: number
  hostAddress: number
  version: string
  timeoutMs?: number
}

// Zintis LAN 扫描请求
export interface ZintisLanScanRequest {
  subnetPrefix?: string
  devicePort?: number
  startHost?: number
  endHost?: number
  threadPoolSize?: number
  timeoutMs?: number
  queryDataCommand?: number
}

// Zintis LAN 扫描发现的设备
export interface ZintisLanDeviceInfo {
  ipAddress: string
  hostAddress: number
  deviceName: string
  payloadHex: string
}

// Zintis LAN 扫描响应
export interface ZintisLanScanResponse {
  success: boolean
  message: string
  subnetPrefix: string
  scannedCount: number
  matchedCount: number
  devices: ZintisLanDeviceInfo[]
}

// 用户灯色映射
export interface UserLightColor {
  id: number
  userId: string
  color: string
  enabled: number
  remark: string
  createTime: string
  updateTime: string
}

// 拣货单上传（主表）
export interface PickingUpload {
  id: string
  waveNo: string
  userId: string
  userName: string
  detailCount: number
  updateTime: number
  createTime: string
}

// 拣货单明细
export interface PickingUploadDetail {
  id: string
  waveNo: string
  billId: string
  materialCode: string
  batchNo: string
  locationCode: string
  planQuantity: number
  actualQuantity: number
  status: number
  createTime: string
}

// App 版本管理（magic-api 全局 sql-column-case: camel，接口返回驼峰键）
export interface AppVersion {
  id: string
  versionCode: number
  versionName: string
  apkUrl: string
  apkSize: string
  forceUpdate: number
  description: string
  disabled: number
  createDate: string
  sha256?: string
  md5?: string
}

// APK manifest 解析结果（上传后自动填充版本号）
export interface ApkParseInfo {
  versionCode: number
  versionName: string
  packageName: string
  apkSize: string
}

// PDA App 运行日志上传记录（索引行，zip 落盘 upload.dir/app-log/{deviceId}/）
export interface AppDeviceLog {
  id: number
  deviceId: string
  appVersion: string | null
  fileName: string
  fileSize: number | null
  uploadedAt: string
  createTime: string
  fileExists: boolean
}

// Zintis Netty 服务状态
export interface ZintisNettyServerStatus {
  running: boolean
  port: number
  activeConnections: number
  heartbeatEnabled: boolean
  heartbeatRunning: boolean
  clientReportRegistrationEnabled: boolean
  lastHeartbeatAt?: number
  lastHeartbeatTargets: number
  lastHeartbeatSuccessCount: number
  lastHeartbeatFailedCount: number
  message: string
}

// Zintis Netty 客户端列表
export interface ZintisNettyClientList {
  running: boolean
  totalClients: number
  clients: string[]
  clientDetails: Array<{
    remoteAddress: string
    macAddress: string
  }>
  message: string
}

// Zintis Netty 发送请求
export interface ZintisNettySendRequest {
  remoteAddress: string
  macAddress?: string
  payload?: string
  payloadArray?: number[]
  payloadFormat?: 'ascii' | 'hex'
  waitResponse?: boolean
}

// Zintis Netty 广播请求
export interface ZintisNettyBroadcastRequest {
  payload?: string
  payloadArray?: number[]
  payloadFormat?: 'ascii' | 'hex'
}

// Zintis Netty 发送响应中的单个客户端结果
export interface ZintisNettySendResult {
  remoteAddress: string
  received: boolean
  timeout: boolean
  rawResponseHex: string
  payloadAscii: string
  macAddress: string
  ipAddress: string
  crc: string
}

// Zintis Netty 发送/广播响应
export interface ZintisNettySendResponse {
  success: boolean
  message: string
  totalTargets: number
  successCount: number
  failedCount: number
  failedTargets: string[]
  waitResponse: boolean
  responseCount: number
  responses: ZintisNettySendResult[]
}

// PDA App 运行日志上传记录（索引行，zip 落盘 upload.dir/app-log/{deviceId}/）
export interface AppDeviceLog {
  id: number
  deviceId: string
  appVersion: string | null
  fileName: string
  fileSize: number | null
  uploadedAt: string
  createTime: string
  fileExists: boolean
}

// 系统授权状态（/system/license/status）
export interface LicenseStatusView {
  enabled: boolean
  status: string          // ok/warning/grace/expired/abnormal/missing/disabled
  message: string
  customer: string | null
  expireAt: string | null
  remainDays: number | null
  graceDays: number
  fingerprints: string    // 3 行指纹块文本
  serverTime: string
}
