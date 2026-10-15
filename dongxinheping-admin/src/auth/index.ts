const TOKEN_KEY = 'dxhp_admin_token'
const USER_KEY = 'dxhp_admin_user'

/** 'admin' 为管理员标记；其他值为角色显示名（后端按用户第一个绑定角色名下发，无绑定为「运营」） */
export type Role = string

export interface SessionUser {
  id: string
  username: string
  name: string | null
  role: Role
  /** 后端下发的可见路由名清单；['*'] 表示全量（admin）。老会话可能缺该字段，启动时会经 validateToken 刷新 */
  menus?: string[]
  /** 后端下发的按钮权限点清单（`路由:动作`）；['*'] 全量；缺失视为全量（兼容旧会话） */
  buttons?: string[]
}

export function setSession(token: string, user: SessionUser): void {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

/** 仅刷新会话中的用户信息（token 不变），供启动时 validateToken 回写 */
export function updateSessionUser(user: SessionUser): void {
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
  // 无用户信息时按低权限显示名处理
  return getCurrentUser()?.role ?? '运营'
}

/** 当前账号是否可访问该路由（菜单清单由后端下发，'*' 表示全量） */
export function canAccess(routeName: string): boolean {
  const menus = getCurrentUser()?.menus
  if (menus == null) return routeName === 'Dashboard'
  return menus.includes('*') || menus.includes(routeName)
}

/** 当前账号是否可用该操作按钮（key 形如 'AdminUser:delete'；缺失视为全量，兼容旧会话） */
export function canButton(key: string): boolean {
  const buttons = getCurrentUser()?.buttons
  if (buttons == null) return true
  return buttons.includes('*') || buttons.includes(key)
}
