import { request } from '../utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'POST',
    data
  })
}

export function register(data) {
  return request({
    url: '/auth/register',
    method: 'POST',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/auth/password',
    method: 'PUT',
    data
  })
}

export function getCurrentUser() {
  return request({
    url: '/auth/me'
  })
}

export function updateProfile(data) {
  return request({
    url: '/auth/profile',
    method: 'PUT',
    data
  })
}
