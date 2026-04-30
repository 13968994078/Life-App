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

async function main() {
  const pages = read('pages.json')
  const index = read('pages/index/index.vue')
  const taxPage = read('pages/tools/tax-calculator.vue')
  const calculatorPath = path.join(__dirname, '..', 'utils', 'tax-calculator.js')
  const calculatorSource = fs.readFileSync(calculatorPath, 'utf8')

  ;[
    'pages/tools/tax-calculator',
    'navigationBarTitleText": "个税计算器"'
  ].forEach((marker) => assertIncludes(pages, marker, 'frontend/pages.json'))

  ;[
    '生活工具',
    '个税计算器',
    '/pages/tools/tax-calculator',
    'tool-grid',
    'tax-tool-card'
  ].forEach((marker) => assertIncludes(index, marker, 'frontend/pages/index/index.vue'))

  ;[
    'calculateMonthlyPayrollTax',
    '本月应预扣税额',
    '累计预扣预缴',
    '税后到手',
    '5000'
  ].forEach((marker) => assertIncludes(taxPage, marker, 'frontend/pages/tools/tax-calculator.vue'))

  ;[
    'TAX_BRACKETS',
    'quickDeduction: 2520',
    'rate: 0.1',
    'calculateMonthlyPayrollTax'
  ].forEach((marker) => assertIncludes(calculatorSource, marker, 'frontend/utils/tax-calculator.js'))

  const { calculateMonthlyPayrollTax } = loadCalculator(calculatorSource)

  const january = calculateMonthlyPayrollTax({
    month: 1,
    currentIncome: 8000,
    currentSocialSecurity: 0,
    currentSpecialAdditionalDeduction: 0,
    currentOtherDeduction: 0,
    previousIncome: 0,
    previousSocialSecurity: 0,
    previousSpecialAdditionalDeduction: 0,
    previousOtherDeduction: 0,
    previousTaxPaid: 0,
    taxReduction: 0
  })
  assertClose(january.taxableIncome, 3000, 'January taxable income')
  assertClose(january.currentTax, 90, 'January current tax')
  assertClose(january.afterTaxIncome, 7910, 'January after-tax income')

  const bracketTwo = calculateMonthlyPayrollTax({
    month: 8,
    currentIncome: 10000,
    previousIncome: 70000,
    previousTaxPaid: 0
  })
  assertClose(bracketTwo.taxableIncome, 40000, 'Bracket two taxable income')
  assertClose(bracketTwo.taxRate, 0.1, 'Bracket two tax rate')
  assertClose(bracketTwo.quickDeduction, 2520, 'Bracket two quick deduction')
  assertClose(bracketTwo.currentTax, 1480, 'Bracket two current tax')

  const overpaid = calculateMonthlyPayrollTax({
    month: 1,
    currentIncome: 8000,
    previousTaxPaid: 200
  })
  assertClose(overpaid.currentTax, 0, 'Overpaid current tax')

  console.log('tax calculator verification passed')
}

function loadCalculator(source) {
  const executableSource = source.replace(/\bexport\s+/g, '')
  return new Function(`${executableSource}\nreturn { calculateMonthlyPayrollTax, TAX_BRACKETS }`)()
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
