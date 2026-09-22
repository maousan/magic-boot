import client from './request'
import { sha256 } from 'js-sha256'
import type { ApiResponse } from '@/types'
import type { SessionUser } from '@/auth'

export interface LoginResult extends SessionUser {
  token: string
}

/** 登录：服务端存 sha256(明文)，传输同样传 sha256(明文) */
export function apiLogin(username: string, password: string): Promise<LoginResult> {
  return client
    .post<ApiResponse<LoginResult>>('/system/security/login', { username, password: sha256(password) })
    .then((r) => {
      if (!r.data.data?.token) {
        throw new Error(r.data.message || '登录失败')
      }
      return r.data.data
    })
}

/** 校验 token，返回当前用户信息（失效时后端返回 code 402，由拦截器统一处理） */
export function apiValidateToken(): Promise<SessionUser> {
  return client.get<ApiResponse<SessionUser>>('/system/security/validateToken').then((r) => r.data.data)
}

export function apiLogout(): Promise<void> {
  return client.get('/system/security/logout').then(
    () => {},
    () => {},
  )
}

/** 自助修改密码：校验原密码，成功后该账号全部会话失效需重新登录。传输均为 sha256(明文) */
export function apiChangeMyPassword(oldPassword: string, newPassword: string): Promise<void> {
  return client
    .post<ApiResponse<boolean>>('/system/user/change-password', {
      oldPassword: sha256(oldPassword),
      newPassword: sha256(newPassword),
    })
    .then(() => {})
}
