import { normalizeAngle } from './wheel-geometry.js'

function toSignedAngleDeg(angle) {
  const normalized = normalizeAngle(angle)
  return normalized > 180 ? normalized - 360 : normalized
}

function normalizeRelativeRotationDeg(angle) {
  const normalized = ((angle + 180) % 360 + 360) % 360 - 180
  return normalized === 180 ? -180 : normalized
}

export function getReadableTangentRotationDeg(angle) {
  const signed = toSignedAngleDeg(angle)
  if (signed > 90) {
    return signed - 180
  }
  if (signed <= -90) {
    return signed + 180
  }
  return signed
}

export function getWheelLabelWrapRotationDeg(angle, wheelRotationDeg = 0) {
  const worldAngle = normalizeAngle(angle + wheelRotationDeg)
  const normalizedWheelRotation = normalizeAngle(wheelRotationDeg)
  return normalizeRelativeRotationDeg(getReadableTangentRotationDeg(worldAngle) - angle - normalizedWheelRotation)
}
