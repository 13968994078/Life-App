const TOKEN_KEY = 'authToken'
const USER_KEY = 'authUser'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY)
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function clearToken() {
  uni.removeStorageSync(TOKEN_KEY)
}

export function getAuthUser() {
  return uni.getStorageSync(USER_KEY) || null
}

export function setAuthUser(user) {
  uni.setStorageSync(USER_KEY, user)
}

export function clearAuthUser() {
  uni.removeStorageSync(USER_KEY)
}

export function clearSession() {
  clearToken()
  clearAuthUser()
}

export function logout() {
  clearSession()
}
