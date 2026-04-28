const assert = require('assert')

function approxEqual(actual, expected, message) {
  const delta = Math.abs(actual - expected)
  assert(delta < 1e-9, `${message}: expected ${expected}, received ${actual}`)
}

async function main() {
  const {
    getReadableTangentRotationDeg,
    getWheelLabelWrapRotationDeg
  } = await import('../utils/wheel-label-orientation.js')

  const scenarios = [
    { angle: 0, world: 0, wrap: 0 },
    { angle: 45, world: 45, wrap: 0 },
    { angle: 90, world: 90, wrap: 0 },
    { angle: 135, world: -45, wrap: -180 },
    { angle: 180, world: 0, wrap: -180 },
    { angle: 225, world: 45, wrap: -180 },
    { angle: 270, world: 90, wrap: -180 },
    { angle: 315, world: -45, wrap: 0 }
  ]

  scenarios.forEach(({ angle, world, wrap }) => {
    approxEqual(getReadableTangentRotationDeg(angle), world, `world rotation mismatch for ${angle}deg`)
    approxEqual(getWheelLabelWrapRotationDeg(angle), wrap, `wrap rotation mismatch for ${angle}deg`)
  })

  const rotatedScenarios = [
    { localAngle: 0, wheelRotation: 100 },
    { localAngle: 51.42857142857143, wheelRotation: 205.71428571428572 },
    { localAngle: 205.71428571428572, wheelRotation: 102.85714285714286 }
  ]

  rotatedScenarios.forEach(({ localAngle, wheelRotation }) => {
    const wrap = getWheelLabelWrapRotationDeg(localAngle, wheelRotation)
    const worldAngle = localAngle + wheelRotation
    const actualWorldRotation = ((localAngle + wheelRotation + wrap + 180) % 360 + 360) % 360 - 180
    const expectedWorldRotation = getReadableTangentRotationDeg(worldAngle)

    approxEqual(
      actualWorldRotation,
      expectedWorldRotation,
      `rotated wheel should keep readable tangent text for local ${localAngle}deg at wheel rotation ${wheelRotation}deg`
    )
  })

  console.log('food wheel label orientation verification passed')
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
