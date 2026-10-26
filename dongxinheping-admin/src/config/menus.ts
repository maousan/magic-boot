import type { MenuOption } from 'naive-ui'

export interface MenuNode {
  label: string
  key: string
  children?: MenuNode[]
}

/**
 * 管理后台菜单目录：AdminLayout 侧边栏与「菜单权限」设置页共用。
 * 叶子节点 key = 路由 name（菜单权限按它配置）；分组 key 仅作标识，不参与权限。
 */
export const menuNodes: MenuNode[] = [
  { label: '仪表盘', key: 'Dashboard' },
  { label: '库位管理', key: 'location', children: [
    { label: '库位列表', key: 'WarehouseLocation' },
    { label: '巷道灯绑定', key: 'LedMapping' },
    { label: '标签绑定', key: 'LabelMapping' },
    { label: '库存查询', key: 'InventoryQuery' },
  ]},
  { label: '巷道灯管理', key: 'led', children: [
    { label: '设备管理', key: 'LedDevice' },
    { label: '颜色管理', key: 'LedColor' },
    { label: 'Netty 管理', key: 'ZintisNetty' },
  ]},
  { label: 'EPC 管理', key: 'epc', children: [
    { label: '批次 EPC 绑定', key: 'BatchEpc' },
    { label: 'RFID Server', key: 'RfidServer' },
  ]},
  { label: '拣货管理', key: 'picking', children: [
    { label: '拣货单', key: 'PickingUpload' },
    { label: '用户灯色映射', key: 'UserLightColor' },
  ]},
  { label: '系统管理', key: 'system', children: [
    { label: '用户管理', key: 'AdminUser' },
    { label: '角色管理', key: 'RoleManagement' },
    { label: '菜单权限', key: 'MenuPermission' },
    { label: '系统配置', key: 'SystemConfig' },
    { label: 'App 版本管理', key: 'AppVersion' },
    { label: 'App 运行日志', key: 'AppDeviceLog' },
    { label: '系统授权', key: 'SystemLicense' },
    { label: '授权签发', key: 'LicenseIssue' },
    { label: '数据库管理', key: 'DatabaseManagement' },
    { label: '定时任务', key: 'JobManagement' },
    { label: 'AIMS 配置', key: 'AimsConfig' },
    { label: 'Magic-API', key: 'MagicApiConsole' },
    { label: '实时日志', key: 'RealtimeLog' },
  ]},
]

/** 全部叶子（路由名）清单 */
export const allMenuRouteKeys: string[] = menuNodes.flatMap((n) =>
  n.children ? n.children.map((c) => c.key) : [n.key],
)

/** AdminLayout 侧边栏直接使用的形态（naive-ui 联合类型在此统一适配） */
export const menuOptions = menuNodes as unknown as MenuOption[]
