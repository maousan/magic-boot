const TOKEN_KEY = 'dxhp_admin_token'
const USER_KEY = 'dxhp_admin_user'

export type Role = 'admin' | 'ops'

export interface SessionUser {
  id: string
  username: string
  name: string | null
  role: Role
}

/** 运营角色可见的路由；admin 全量可见 */
const OPS_ALLOWED_ROUTES: ReadonlySet<string> = new Set([
  'Dashboard',
  // 库存管理（库位管理组）
  'WarehouseLocation',
  'LedMapping',
  'LabelMapping',
  'InventoryQuery',
  // 巷道灯管理
  'LedDevice',
  'LedColor',
  'ZintisNetty',
  // EPC 管理
  'BatchEpc',
  'RfidServer',
  // 拣货管理
  'PickingUpload',
  'UserLightColor',
])

export function setSession(token: string, user: SessionUser): void {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearSession(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function getCurrentUser(): SessionUser | null {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? (JSON.parse(raw) as SessionUser) : null
  } catch {
    return null
  }
}

export function isAuthenticated(): boolean {
  return !!getToken()
}

export function getRole(): Role {
  // 无用户信息时按低权限角色处理
  return getCurrentUser()?.role ?? 'ops'
}

/** 当前角色是否可访问该路由 */
export function canAccess(routeName: string): boolean {
  if (getRole() === 'admin') return true
  return OPS_ALLOWED_ROUTES.has(routeName)
}
