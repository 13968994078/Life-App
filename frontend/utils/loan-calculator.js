export function calculateLoan(input = {}) {
  const principal = money(input.amountWan) * 10000
  const years = normalizeYears(input.years)
  const months = years * 12
  const monthlyRate = money(input.annualRate) / 100 / 12
  const repaymentMethod = input.repaymentMethod === 'equalPrincipal' ? 'equalPrincipal' : 'equalPayment'

  if (!principal || !months) {
    return buildResult({ principal, months, repaymentMethod })
  }

  if (monthlyRate === 0) {
    const monthlyPayment = principal / months
    return buildResult({
      principal,
      months,
      repaymentMethod,
      firstMonthlyPayment: monthlyPayment,
      averageMonthlyPayment: monthlyPayment,
      monthlyDecrease: 0,
      totalInterest: 0,
      totalRepayment: principal
    })
  }

  if (repaymentMethod === 'equalPrincipal') {
    return calculateEqualPrincipal(principal, months, monthlyRate, repaymentMethod)
  }

  return calculateEqualPayment(principal, months, monthlyRate, repaymentMethod)
}

function calculateEqualPayment(principal, months, monthlyRate, repaymentMethod) {
  const factor = Math.pow(1 + monthlyRate, months)
  const monthlyPayment = principal * monthlyRate * factor / (factor - 1)
  const totalRepayment = monthlyPayment * months
  return buildResult({
    principal,
    months,
    repaymentMethod,
    firstMonthlyPayment: monthlyPayment,
    averageMonthlyPayment: monthlyPayment,
    monthlyDecrease: 0,
    totalInterest: totalRepayment - principal,
    totalRepayment
  })
}

function calculateEqualPrincipal(principal, months, monthlyRate, repaymentMethod) {
  const monthlyPrincipal = principal / months
  const firstMonthlyPayment = monthlyPrincipal + principal * monthlyRate
  const monthlyDecrease = monthlyPrincipal * monthlyRate
  const totalInterest = monthlyRate * monthlyPrincipal * months * (months + 1) / 2
  return buildResult({
    principal,
    months,
    repaymentMethod,
    firstMonthlyPayment,
    averageMonthlyPayment: (principal + totalInterest) / months,
    monthlyDecrease,
    totalInterest,
    totalRepayment: principal + totalInterest
  })
}

function buildResult({
  principal = 0,
  months = 0,
  repaymentMethod = 'equalPayment',
  firstMonthlyPayment = 0,
  averageMonthlyPayment = 0,
  monthlyDecrease = 0,
  totalInterest = 0,
  totalRepayment = 0
} = {}) {
  return {
    principal: roundMoney(principal),
    months,
    repaymentMethod,
    firstMonthlyPayment: roundMoney(firstMonthlyPayment),
    averageMonthlyPayment: roundMoney(averageMonthlyPayment),
    monthlyDecrease: roundMoney(monthlyDecrease),
    totalInterest: roundMoney(totalInterest),
    totalRepayment: roundMoney(totalRepayment)
  }
}

function normalizeYears(value) {
  const parsed = parseInt(value, 10)
  if (Number.isNaN(parsed)) {
    return 1
  }
  return Math.min(Math.max(parsed, 1), 40)
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
