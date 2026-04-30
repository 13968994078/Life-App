import { request } from '../utils/request'

export function getHomeContent() {
  return request({ url: '/content/home' })
}
