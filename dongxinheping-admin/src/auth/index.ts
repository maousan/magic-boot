const AUTH_KEY = 'dxhp_admin_auth'

export function login(username: string, password: string): boolean {
  const validUser = import.meta.env.VITE_ADMIN_USER
  const validPass = import.meta.env.VITE_ADMIN_PASS
  if (username === validUser && password === validPass) {
    localStorage.setItem(AUTH_KEY, 'authenticated')
    return true
  }
  return false
}

export function logout(): void {
  localStorage.removeItem(AUTH_KEY)
}

export function isAuthenticated(): boolean {
  return localStorage.getItem(AUTH_KEY) === 'authenticated'
}
