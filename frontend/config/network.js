export const DEFAULT_API_BASE_URL = 'http://47.97.40.183/api'

export function getApiBaseUrl() {
  const stored = uni.getStorageSync('apiBaseUrl')
  return stored || DEFAULT_API_BASE_URL
}

export function setApiBaseUrl(url) {
  uni.setStorageSync('apiBaseUrl', url)
}

export function clearApiBaseUrl() {
  uni.removeStorageSync('apiBaseUrl')
}
