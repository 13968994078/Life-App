const assert = require('assert')
const fs = require('fs')
const path = require('path')

function read(relPath) {
  return fs.readFileSync(path.join(__dirname, '..', relPath), 'utf8')
}

function assertIncludes(content, marker, label) {
  assert(content.includes(marker), `${label} should include "${marker}"`)
}

function main() {
  const authApi = read('api/auth.js')
  const checkinApi = read('api/checkin.js')
  const loginPage = read('pages/login/index.vue')
  const minePage = read('pages/mine/index.vue')
  const profilePage = read('pages/mine/profile.vue')
  const passwordPage = read('pages/mine/password.vue')
  const checkinPage = read('pages/checkin/index.vue')

  ;[
    'export function register',
    'export function changePassword'
  ].forEach((marker) => assertIncludes(authApi, marker, 'frontend/api/auth.js'))

  assertIncludes(checkinApi, 'export function getPublicCheckinBoard', 'frontend/api/checkin.js')

  ;[
    'auth-mode-switch',
    'register-panel',
    'confirm-password-field'
  ].forEach((marker) => assertIncludes(loginPage, marker, 'frontend/pages/login/index.vue'))

  ;[
    '/pages/mine/profile'
  ].forEach((marker) => assertIncludes(minePage, marker, 'frontend/pages/mine/index.vue'))

  ;[
    'account-value',
    'profile-avatar-options'
  ].forEach((marker) => assertIncludes(profilePage, marker, 'frontend/pages/mine/profile.vue'))

  ;[
    'password-sheet',
    'password-form',
    'changePassword'
  ].forEach((marker) => assertIncludes(passwordPage, marker, 'frontend/pages/mine/password.vue'))

  ;[
    'public-board-card',
    'board-row',
    'board-avatar-wrap',
    'boardAvatarLetter',
    'self-badge'
  ].forEach((marker) => assertIncludes(checkinPage, marker, 'frontend/pages/checkin/index.vue'))

  console.log('multi-user auth verification passed')
}

main()
