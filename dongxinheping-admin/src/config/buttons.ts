/**
 * 按钮权限点注册表：key = `${路由name}:${action}`，登录时后端按角色下发
 * buttons 清单（['*'] 全量；未配置 NULL 视为全量，兼容存量角色）。
 * 页面里用 v-perm="'AdminUser:delete'" 或 canButton('AdminUser:delete') 控制。
 */
export interface ButtonDef {
  action: string
  label: string
}

export const buttonKey = (route: string, action: string): string => `${route}:${action}`

/** 各路由（叶子菜单）可配置的操作按钮；未登记的页面视为纯查看页 */
export const routeButtons: Record<string, ButtonDef[]> = {
  AdminUser: [
    { action: 'create', label: '新增用户' },
    { action: 'update', label: '编辑' },
    { action: 'resetPassword', label: '重置密码' },
    { action: 'toggle', label: '禁用/解禁' },
    { action: 'delete', label: '删除' },
  ],
  RoleManagement: [
    { action: 'create', label: '新增角色' },
    { action: 'update', label: '编辑' },
    { action: 'members', label: '成员管理' },
    { action: 'delete', label: '删除' },
  ],
  MenuPermission: [{ action: 'save', label: '保存配置' }],
  SystemConfig: [{ action: 'save', label: '保存配置' }],
  WarehouseLocation: [
    { action: 'create', label: '手动添加' },
    { action: 'update', label: '编辑' },
    { action: 'import', label: '批量导入' },
    { action: 'delete', label: '删除' },
  ],
  LedMapping: [
    { action: 'create', label: '新增绑定' },
    { action: 'import', label: '导入' },
    { action: 'delete', label: '删除' },
  ],
  LabelMapping: [
    { action: 'create', label: '新增绑定' },
    { action: 'unbind', label: '解绑' },
    { action: 'light', label: '亮灯/灭灯' },
    { action: 'delete', label: '删除' },
  ],
  InventoryQuery: [{ action: 'maintain', label: '数据维护（录入/清空/模拟）' }],
  LedDevice: [
    { action: 'control', label: '灯光控制' },
    { action: 'server', label: '开关 Client/Server' },
    { action: 'ota', label: 'OTA 升级' },
    { action: 'update', label: '编辑设备' },
    { action: 'delete', label: '删除' },
  ],
  LedColor: [
    { action: 'create', label: '新增颜色' },
    { action: 'update', label: '编辑' },
    { action: 'delete', label: '删除' },
  ],
  ZintisNetty: [
    { action: 'server', label: '启停 Server' },
    { action: 'client', label: '开关 Client' },
    { action: 'control', label: '灯光控制' },
    { action: 'ota', label: 'OTA 升级' },
    { action: 'command', label: '发送/广播指令' },
  ],
  BatchEpc: [
    { action: 'bind', label: '绑定' },
    { action: 'update', label: '编辑' },
    { action: 'unbind', label: '解绑' },
    { action: 'delete', label: '删除' },
  ],
  RfidServer: [
    { action: 'server', label: '启停' },
    { action: 'command', label: '发送指令' },
  ],
  PickingUpload: [
    { action: 'create', label: '新增单据/明细' },
    { action: 'update', label: '编辑' },
    { action: 'delete', label: '删除' },
    { action: 'upload', label: '推送上传' },
    { action: 'light', label: '开灯/关灯' },
    { action: 'mock', label: '生成模拟单据' },
  ],
  UserLightColor: [
    { action: 'create', label: '新增/编辑' },
    { action: 'delete', label: '删除' },
  ],
  AppVersion: [
    { action: 'create', label: '新增版本' },
    { action: 'update', label: '修改' },
    { action: 'delete', label: '删除' },
  ],
  AppDeviceLog: [{ action: 'delete', label: '删除日志' }],
  JobManagement: [{ action: 'control', label: '执行/暂停/恢复' }],
  AimsConfig: [{ action: 'save', label: '保存配置' }],
  SystemLicense: [{ action: 'import', label: '导入授权文件' }],
  LicenseIssue: [{ action: 'issue', label: '签发授权' }],
}

/** 全部按钮权限点（校验/全量勾选用） */
export const allButtonKeys: string[] = Object.entries(routeButtons).flatMap(([route, defs]) =>
  defs.map((d) => buttonKey(route, d.action)),
)
