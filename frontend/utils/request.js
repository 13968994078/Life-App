import { getApiBaseUrl } from '../config/network'
import { clearSession, getToken } from './auth'

let redirectingToLogin = false

export function request({ url, method = 'GET', data }) {
  return new Promise((resolve, reject) => {
    const baseUrl = getApiBaseUrl()

    uni.request({
      url: `${baseUrl}${url}`,
      method,
      data,
      timeout: 10000,
      header: buildHeaders(),
      success: (res) => {
        const payload = res.data || {}
        if (res.statusCode === 401) {
          handleUnauthorized(url)
          reject(new Error(payload.message || '请先登录'))
          return
        }
        if (payload.success) {
          resolve(payload.data)
          return
        }
        reject(new Error(payload.message || 'Request failed'))
      },
      fail: (error) => {
        reject(new Error(error.errMsg || '网络请求失败'))
      }
    })
  })
}

function buildHeaders() {
  const token = getToken()
  if (!token) {
    return {}
  }

  return {
    Authorization: `Bearer ${token}`
  }
}

function handleUnauthorized(url) {
  if (url === '/auth/login' || url === '/auth/register') {
    return
  }

  clearSession()
  if (redirectingToLogin) {
    return
  }

  const currentPages = getCurrentPages()
  const currentPage = currentPages[currentPages.length - 1]
  const currentRoute = currentPage ? `/${currentPage.route}` : '/pages/mine/index'
  if (currentRoute.startsWith('/pages/login/index')) {
    return
  }

  redirectingToLogin = true
  const loginUrl = `/pages/login/index?redirect=${encodeURIComponent(currentRoute)}`

  uni.showToast({ title: '登录已失效，请重新登录', icon: 'none' })

  setTimeout(() => {
    uni.navigateTo({
      url: loginUrl,
      complete: () => {
        redirectingToLogin = false
        }
      })
  }, 200)
}
