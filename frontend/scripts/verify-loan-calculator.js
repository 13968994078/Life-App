const assert = require('assert')
const fs = require('fs')
const path = require('path')

function read(relPath) {
  return fs.readFileSync(path.join(__dirname, '..', relPath), 'utf8')
}

function assertIncludes(content, marker, label) {
  assert(content.includes(marker), `${label} should include "${marker}"`)
}

function assertClose(actual, expected, label) {
  assert(Math.abs(actual - expected) < 0.01, `${label} expected ${expected}, got ${actual}`)
}

function loadCalculator(source) {
  const executableSource = source.replace(/\bexport\s+/g, '')
  return new Function(`${executableSource}\nreturn { calculateLoan }`)()
}

function main() {
  const pages = read('pages.json')
  const index = read('pages/index/index.vue')
  const loanPage = read('pages/tools/loan-calculator.vue')
  const calculatorPath = path.join(__dirname, '..', 'utils', 'loan-calculator.js')
  const calculatorSource = fs.readFileSync(calculatorPath, 'utf8')

  ;[
    'pages/tools/loan-calculator',
    'navigationBarTitleText": "贷款计算器"'
  ].forEach((marker) => assertIncludes(pages, marker, 'frontend/pages.json'))

  ;[
    '生活工具',
    '贷款计算器',
    '/pages/tools/loan-calculator',
    'loan-tool-card'
  ].forEach((marker) => assertIncludes(index, marker, 'frontend/pages/index/index.vue'))

  ;[
    'calculateLoan',
    '等额本息',
    '等额本金',
    '首月月供',
    '每月递减',
    '总利息',
    '还款总额'
  ].forEach((marker) => assertIncludes(loanPage, marker, 'frontend/pages/tools/loan-calculator.vue'))

  ;[
    'calculateLoan',
    'equalPayment',
    'equalPrincipal',
    'monthlyRate',
    'totalInterest'
  ].forEach((marker) => assertIncludes(calculatorSource, marker, 'frontend/utils/loan-calculator.js'))

  const { calculateLoan } = loadCalculator(calculatorSource)

  const equalPayment = calculateLoan({
    amountWan: 100,
    annualRate: 3.6,
    years: 30,
    repaymentMethod: 'equalPayment'
  })
  assertClose(equalPayment.months, 360, 'Equal payment months')
  assertClose(equalPayment.firstMonthlyPayment, 4546.45, 'Equal payment monthly payment')
  assertClose(equalPayment.monthlyDecrease, 0, 'Equal payment monthly decrease')
  assertClose(equalPayment.totalInterest, 636723.26, 'Equal payment total interest')
  assertClose(equalPayment.totalRepayment, 1636723.26, 'Equal payment total repayment')

  const equalPrincipal = calculateLoan({
    amountWan: 100,
    annualRate: 3.6,
    years: 30,
    repaymentMethod: 'equalPrincipal'
  })
  assertClose(equalPrincipal.firstMonthlyPayment, 5777.78, 'Equal principal first payment')
  assertClose(equalPrincipal.monthlyDecrease, 8.33, 'Equal principal monthly decrease')
  assertClose(equalPrincipal.totalInterest, 541500, 'Equal principal total interest')
  assertClose(equalPrincipal.totalRepayment, 1541500, 'Equal principal total repayment')

  const zeroRate = calculateLoan({
    amountWan: 12,
    annualRate: 0,
    years: 1,
    repaymentMethod: 'equalPayment'
  })
  assertClose(zeroRate.firstMonthlyPayment, 10000, 'Zero rate monthly payment')
  assertClose(zeroRate.totalInterest, 0, 'Zero rate total interest')
  assertClose(zeroRate.totalRepayment, 120000, 'Zero rate total repayment')

  console.log('loan calculator verification passed')
}

main()
