const assert = require('assert')

async function main() {
  const { getWheelLabelLayout } = await import('../utils/wheel-label-layout.js')

  const layout4 = getWheelLabelLayout(4)
  const layout7 = getWheelLabelLayout(7)
  const layout8 = getWheelLabelLayout(8)
  const layout10 = getWheelLabelLayout(10)

  assert(layout4.labelRadius >= 150, `4 items should place labels farther out, received ${layout4.labelRadius}`)
  assert(layout7.labelRadius >= 134, `7 items should move labels farther outward, received ${layout7.labelRadius}`)
  assert(layout7.labelWidth >= 108, `7 items should gain more horizontal room, received ${layout7.labelWidth}`)
  assert(layout7.maxChars >= 7, `7 items should show at least 7 chars before truncation, received ${layout7.maxChars}`)
  assert(layout8.maxChars >= 6, `8 items should show at least 6 chars before truncation, received ${layout8.maxChars}`)
  assert(layout10.labelWidth >= 72, `dense wheels should still keep readable width, received ${layout10.labelWidth}`)

  console.log('food wheel label layout verification passed')
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
