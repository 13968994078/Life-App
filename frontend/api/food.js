import { request } from '../utils/request'

export function getFoodList(category) {
  const data = {}
  if (category) {
    data.category = category
  }

  return request({
    url: '/food/list',
    data: Object.keys(data).length ? data : undefined
  })
}

export function randomFood(category) {
  const data = {}
  if (category) {
    data.category = category
  }

  return request({
    url: '/food/random',
    method: 'POST',
    data
  })
}

export function getFoodHistory() {
  return request({
    url: '/food/history'
  })
}

export function createFood(data) {
  return request({
    url: '/food',
    method: 'POST',
    data
  })
}

export function updateFood(id, data) {
  return request({
    url: `/food/${id}`,
    method: 'PUT',
    data
  })
}

export function deleteFood(id) {
  return request({
    url: `/food/${id}`,
    method: 'DELETE'
  })
}
