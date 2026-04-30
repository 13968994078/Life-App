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
  const pages = read('pages.json')
  const authApi = read('api/auth.js')
  const login = read('pages/login/index.vue')
  const mine = read('pages/mine/index.vue')
  const profile = read('pages/mine/profile.vue')
  const password = read('pages/mine/password.vue')

  assertIncludes(authApi, 'export function updateProfile', 'frontend/api/auth.js')
  assertIncludes(pages, 'pages/mine/password', 'frontend/pages.json')
  assertIncludes(pages, 'pages/mine/profile', 'frontend/pages.json')

  ;[
    "uni.switchTab({ url: '/pages/index/index' })",
    'navigateAfterLogin()'
  ].forEach((marker) => assertIncludes(login, marker, 'frontend/pages/login/index.vue'))

  ;[
    'redirectUrl',
    'decodeURIComponent(options.redirect)',
    'redirectTo({ url: this.redirectUrl })'
  ].forEach((marker) => assertNotIncludes(login, marker, 'frontend/pages/login/index.vue'))

  ;[
    'stats-grid',
    'status-card',
    'activity-list',
    'management-list',
    'rankText',
    'getCheckinStatistics',
    'getPublicCheckinBoard',
    'getFoodHistory',
    '/pages/mine/profile',
    '/pages/mine/password',
    '个人资料',
    '修改密码',
    '签到席位'
  ].forEach((marker) => assertIncludes(mine, marker, 'frontend/pages/mine/index.vue'))

  ;[
    'profile-edit-form',
    'handleSaveProfile',
    'updateProfile',
    'avatarUrl',
    'avatarLetter'
  ].forEach((marker) => assertNotIncludes(mine, marker, 'frontend/pages/mine/index.vue'))

  ;[
    'profile-page',
    'profile-color-refresh',
    'profile-edit-form',
    'avatar-options',
    'profile-avatar-options',
    'profile-hero',
    'profile-card',
    'default-avatar-option',
    'handleSaveProfile',
    'updateProfile',
    'getCurrentUser',
    'setAuthUser',
    "return this.profileForm.avatar || ''",
    'static/profile-avatars/',
    'avatar-wheat.png',
    'avatar-clock.png',
    'avatar-rice.png',
    '默认头像',
    '先填写昵称'
  ].forEach((marker) => assertIncludes(profile, marker, 'frontend/pages/mine/profile.vue'))

  ;[
    'avatar-library-first',
    'compact-profile-preview',
    'avatar-filter-tabs',
    'filteredAvatarOptions',
    'activeAvatarGroup',
    'label-inline'
  ].forEach((marker) => assertNotIncludes(profile, marker, 'frontend/pages/mine/profile.vue'))

  ;[
    '多用户数据隔离',
    '数据范围',
    '已登录',
    '账号安全',
    '首字头像',
    'achievement-grid'
  ].forEach((marker) => assertNotIncludes(mine, marker, 'frontend/pages/mine/index.vue'))

  ;[
    'password-sheet',
    'password-form',
    'handleChangePassword',
    'changePassword',
    'form-label-with-icon'
  ].forEach((marker) => assertIncludes(password, marker, 'frontend/pages/mine/password.vue'))

  console.log('mine dashboard verification passed')
}

main()
