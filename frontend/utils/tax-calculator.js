export const TAX_BRACKETS = [
  { max: 36000, rate: 0.03, quickDeduction: 0 },
  { max: 144000, rate: 0.1, quickDeduction: 2520 },
  { max: 300000, rate: 0.2, quickDeduction: 16920 },
  { max: 420000, rate: 0.25, quickDeduction: 31920 },
  { max: 660000, rate: 0.3, quickDeduction: 52920 },
  { max: 960000, rate: 0.35, quickDeduction: 85920 },
  { max: Infinity, rate: 0.45, quickDeduction: 181920 }
]

export function calculateMonthlyPayrollTax(input = {}) {
  const month = normalizeMonth(input.month)
  const currentIncome = money(input.currentIncome)
  const currentSocialSecurity = money(input.currentSocialSecurity)
  const currentSpecialAdditionalDeduction = money(input.currentSpecialAdditionalDeduction)
  const currentOtherDeduction = money(input.currentOtherDeduction)
  const previousIncome = money(input.previousIncome)
  const previousSocialSecurity = money(input.previousSocialSecurity)
  const previousSpecialAdditionalDeduction = money(input.previousSpecialAdditionalDeduction)
  const previousOtherDeduction = money(input.previousOtherDeduction)
  const previousTaxPaid = money(input.previousTaxPaid)
  const taxReduction = money(input.taxReduction)

  const cumulativeIncome = previousIncome + currentIncome
  const cumulativeBasicDeduction = 5000 * month
  const cumulativeSocialSecurity = previousSocialSecurity + currentSocialSecurity
  const cumulativeSpecialAdditionalDeduction = previousSpecialAdditionalDeduction + currentSpecialAdditionalDeduction
  const cumulativeOtherDeduction = previousOtherDeduction + currentOtherDeduction
  const taxableIncome = Math.max(
    0,
    cumulativeIncome -
      cumulativeBasicDeduction -
      cumulativeSocialSecurity -
      cumulativeSpecialAdditionalDeduction -
      cumulativeOtherDeduction
  )
  const bracket = findTaxBracket(taxableIncome)
  const cumulativeTax = Math.max(0, taxableIncome * bracket.rate - bracket.quickDeduction - taxReduction)
  const currentTax = Math.max(0, cumulativeTax - previousTaxPaid)
  const afterTaxIncome = currentIncome - currentSocialSecurity - currentTax

  return {
    month,
    cumulativeIncome: roundMoney(cumulativeIncome),
    cumulativeBasicDeduction: roundMoney(cumulativeBasicDeduction),
    cumulativeSocialSecurity: roundMoney(cumulativeSocialSecurity),
    cumulativeSpecialAdditionalDeduction: roundMoney(cumulativeSpecialAdditionalDeduction),
    cumulativeOtherDeduction: roundMoney(cumulativeOtherDeduction),
    taxableIncome: roundMoney(taxableIncome),
    taxRate: bracket.rate,
    quickDeduction: bracket.quickDeduction,
    cumulativeTax: roundMoney(cumulativeTax),
    currentTax: roundMoney(currentTax),
    afterTaxIncome: roundMoney(afterTaxIncome)
  }
}

function findTaxBracket(taxableIncome) {
  return TAX_BRACKETS.find((item) => taxableIncome <= item.max) || TAX_BRACKETS[TAX_BRACKETS.length - 1]
}

function normalizeMonth(value) {
  const parsed = parseInt(value, 10)
  if (Number.isNaN(parsed)) {
    return 1
  }
  return Math.min(Math.max(parsed, 1), 12)
}

function money(value) {
  const parsed = Number(value)
  if (!Number.isFinite(parsed) || parsed < 0) {
    return 0
  }
  return parsed
}

function roundMoney(value) {
  return Math.round((value + Number.EPSILON) * 100) / 100
}
