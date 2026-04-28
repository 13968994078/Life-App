import { request } from '../utils/request'

export function getSettings() {
  return request({ url: '/settings' })
}

export function updateSettings(data) {
  return request({
    url: '/settings',
    method: 'PUT',
    data
  })
}
