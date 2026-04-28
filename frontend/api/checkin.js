import { request } from '../utils/request'

export function getTodayCheckin() {
  return request({ url: '/check-in/today' })
}

export function doCheckin() {
  return request({ url: '/check-in', method: 'POST' })
}

export function getCheckinStatistics() {
  return request({ url: '/check-in/statistics' })
}

export function getCheckinCalendar(year, month) {
  return request({
    url: '/check-in/calendar',
    data: { year, month }
  })
}

export function getPublicCheckinBoard() {
  return request({
    url: '/check-in/public-board'
  })
}
