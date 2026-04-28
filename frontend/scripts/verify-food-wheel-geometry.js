const assert = require('assert')

function approxEqual(actual, expected, message) {
  const delta = Math.abs(actual - expected)
  assert(delta < 1e-9, `${message}: expected ${expected}, received ${actual}`)
}

function verifyDynamicSectorCoverage(geometry, itemCount) {
  const { getDividerDeg, getGradientOffsetDeg, getSectorAngle, getSectorCenterDeg, getSectorEndDeg, getSectorStartDeg } = geometry
  const sectorAngle = getSectorAngle(itemCount)
  approxEqual(sectorAngle * itemCount, 360, `${itemCount} items should cover 360 degrees`)
  approxEqual(getGradientOffsetDeg(itemCount), -sectorAngle / 2, `${itemCount} items should offset the gradient by half a sector`)

  for (let index = 0; index < itemCount; index += 1) {
    const start = getSectorStartDeg(index, itemCount)
    const end = getSectorEndDeg(index, itemCount)
    const center = getSectorCenterDeg(index, itemCount)
    approxEqual(center, (start + end) / 2, `sector ${index} center should be the midpoint`)
    approxEqual(getDividerDeg(index, itemCount), start, `sector ${index} divider should match its start angle`)
  }
}

function verifyPointerLanding(geometry, itemCount) {
  const { getSectorCenterDeg, getTargetRotationDeg, normalizeAngle } = geometry
  for (let index = 0; index < itemCount; index += 1) {
    const resolved = normalizeAngle(getTargetRotationDeg(index, itemCount) + getSectorCenterDeg(index, itemCount))
    approxEqual(resolved, 0, `sector ${index} should land on the top pointer`)
  }
}

async function main() {
  const geometry = await import('../utils/wheel-geometry.js')

  ;[3, 4, 7, 9].forEach((itemCount) => {
    verifyDynamicSectorCoverage(geometry, itemCount)
    verifyPointerLanding(geometry, itemCount)
  })

  console.log('food wheel geometry verification passed')
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
