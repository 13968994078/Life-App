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
  const uni = read('uni.scss')
  const app = read('App.vue')
  const index = read('pages/index/index.vue')
  const food = read('pages/food/index.vue')
  const foodHistory = read('pages/food/history.vue')
  const checkin = read('pages/checkin/index.vue')
  const mine = read('pages/mine/index.vue')
  const profile = read('pages/mine/profile.vue')
  const login = read('pages/login/index.vue')
  const password = read('pages/mine/password.vue')
  const brandIcon = read('components/brand-icon.vue')

  ;[
    '$paper-ivory',
    '$paper-blush',
    '$ink-strong',
    '$accent-gold',
    '$card-shadow-strong'
  ].forEach((marker) => assertIncludes(uni, marker, 'uni.scss'))

  ;[
    '.section-label',
    '.primary-btn',
    '.picker-surface',
    '.empty-text'
  ].forEach((marker) => assertIncludes(app, marker, 'App.vue'))

  ;[
    'quote-card',
    'summary-strip',
    'action-grid',
    'recent-card'
  ].forEach((marker) => assertIncludes(index, marker, 'pages/index/index.vue'))

  ;[
    'food-hero',
    'food-layout-card',
    'food-creator',
    'creatorAvatarUrl',
    'form-actions',
    'history-dot'
  ].forEach((marker) => assertIncludes(food, marker, 'pages/food/index.vue'))

  ;[
    'archive-stage',
    'archive-summary-strip',
    'archive-list-card'
  ].forEach((marker) => assertIncludes(foodHistory, marker, 'pages/food/history.vue'))

  ;[
    'status-hero',
    'board-card',
    'public-board-card',
    'board-avatar-wrap',
    'boardAvatarLetter',
    'calendar-card',
    'record-card'
  ].forEach((marker) => assertIncludes(checkin, marker, 'pages/checkin/index.vue'))

  ;[
    'profile-hero',
    'management-list',
    'setting-card'
  ].forEach((marker) => assertIncludes(mine, marker, 'pages/mine/index.vue'))

  ;[
    'profile-page',
    'profile-color-refresh',
    'profile-hero',
    'profile-card',
    'profile-head',
    'profile-avatar-options',
    'name="profile"',
    'name="petal"',
    'form-label-with-icon',
    'avatar-wheat.png',
    'avatar-clock.png',
    'avatar-rice.png'
  ].forEach((marker) => assertIncludes(profile, marker, 'pages/mine/profile.vue'))

  ;[
    'login-stage',
    'credential-card',
    'demo-pill',
    "'quote'",
    "'profile'",
    "'key'"
  ].forEach((marker) => assertIncludes(login, marker, 'pages/login/index.vue'))

  ;[
    'name="warning"',
    'name="save"',
    'name="success"',
    'form-label-with-icon'
  ].forEach((marker) => assertIncludes(password, marker, 'pages/mine/password.vue'))

  ;[
    'quoteActive',
    'notificationActive',
    'targetActive',
    'profileActive',
    'teaActive',
    'petalActive'
  ].forEach((marker) => assertIncludes(brandIcon, marker, 'components/brand-icon.vue'))

  ;[
    'name="quote"',
    'name="tea"',
    'name="empty"',
    'name="time"',
    'name="target"',
    'name="notification"',
    'name="profile"',
    'name="petal"'
  ].forEach((marker) => assertIncludes(index + food + checkin + mine, marker, 'icon usage'))

  console.log('ui refresh verification passed')
}

main()
