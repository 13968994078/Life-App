const assert = require('assert')
const fs = require('fs')
const path = require('path')

function read(relPath) {
  return fs.readFileSync(path.join(__dirname, '..', relPath), 'utf8')
}

function assertIncludes(content, marker, label) {
  assert(content.includes(marker), `${label} should include "${marker}"`)
}

function assertNotIncludes(content, marker, label) {
  assert(!content.includes(marker), `${label} should not include "${marker}"`)
}

function main() {
  const login = read('pages/login/index.vue')
  const food = read('pages/food/index.vue')
  const foodApi = read('api/food.js')
  const auth = read('utils/auth.js')
  const request = read('utils/request.js')

  ;[
    'import { getCurrentUser, login, register }',
    'onShow()',
    'await getCurrentUser()'
  ].forEach((marker) => assertIncludes(login, marker, 'frontend/pages/login/index.vue'))

  ;[
    'export function clearSession',
    'clearSession()'
  ].forEach((marker) => assertIncludes(auth, marker, 'frontend/utils/auth.js'))

  ;[
    'clearSession()',
    "url === '/auth/register'"
  ].forEach((marker) => assertIncludes(request, marker, 'frontend/utils/request.js'))

  ;[
    'export function getFoodList(category)',
    'export function randomFood(category)'
  ].forEach((marker) => assertIncludes(foodApi, marker, 'frontend/api/food.js'))

  ;[
    'lastAuthUserId',
    'resetEditStateForAccount',
    'validateEditingPermission',
    'this.resetEditStateForAccount()',
    'this.validateEditingPermission()',
    'canManageFood(item)',
    'String(item.userId) === this.currentUserId',
    'return item.creatorName ||',
    'return item && item.creatorAvatar ? item.creatorAvatar :'
  ].forEach((marker) => assertIncludes(food, marker, 'frontend/pages/food/index.vue'))

  ;[
    'poolViews',
    'poolTypes',
    'currentPoolViewLabel',
    'switchPoolView',
    'selectFormPool',
    'foodPoolCaption',
    'poolTagClass',
    'poolTypeLabel',
    'form.poolType',
    'pool-toggle-row',
    '加入哪个池子',
    '公共区',
    '私有区'
  ].forEach((marker) => assertNotIncludes(food, marker, 'frontend/pages/food/index.vue'))

  assertNotIncludes(foodApi, 'poolView', 'frontend/api/food.js')

  console.log('auth and food hardening verification passed')
}

main()
