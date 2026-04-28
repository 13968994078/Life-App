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
  const login = read('pages/login/index.vue')

  ;[
    '$paper-ivory',
    '$paper-blush',
    '$ink-strong',
    '$accent-gold',
    '$card-shadow-strong'
  ].forEach((marker) => assertIncludes(uni, marker, 'uni.scss'))

  ;[
    '.page::before',
    '.page::after',
    '.editorial-kicker',
    '.editorial-link'
  ].forEach((marker) => assertIncludes(app, marker, 'App.vue'))

  ;[
    'hero-kicker',
    'hero-side-note',
    'feature-lead-grid',
    'editorial-nav-card'
  ].forEach((marker) => assertIncludes(index, marker, 'pages/index/index.vue'))

  ;[
    'stage-card',
    'filter-strip',
    'editorial-form-card',
    'editorial-list-card'
  ].forEach((marker) => assertIncludes(food, marker, 'pages/food/index.vue'))

  ;[
    'archive-stage',
    'archive-summary-strip',
    'archive-list-card'
  ].forEach((marker) => assertIncludes(foodHistory, marker, 'pages/food/history.vue'))

  ;[
    'stage-card',
    'calendar-card',
    'record-card'
  ].forEach((marker) => assertIncludes(checkin, marker, 'pages/checkin/index.vue'))

  ;[
    'account-stage',
    'identity-card',
    'settings-sheet'
  ].forEach((marker) => assertIncludes(mine, marker, 'pages/mine/index.vue'))

  ;[
    'login-stage',
    'credential-card',
    'demo-pill'
  ].forEach((marker) => assertIncludes(login, marker, 'pages/login/index.vue'))

  console.log('ui refresh verification passed')
}

main()
