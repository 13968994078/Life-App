function normalizeItemCount(itemCount) {
  const count = Number(itemCount)
  if (!Number.isFinite(count) || count <= 0) {
    return 1
  }
  return Math.max(1, Math.floor(count))
}

function getSectorAngle(itemCount) {
  return 360 / normalizeItemCount(itemCount)
}

function getSectorCenterDeg(index, itemCount) {
  return getSectorAngle(itemCount) * index
}

function getSectorStartDeg(index, itemCount) {
  return getSectorCenterDeg(index, itemCount) - getSectorAngle(itemCount) / 2
}

function getSectorEndDeg(index, itemCount) {
  return getSectorCenterDeg(index, itemCount) + getSectorAngle(itemCount) / 2
}

function getDividerDeg(index, itemCount) {
  return getSectorStartDeg(index, itemCount)
}

function getGradientOffsetDeg(itemCount) {
  return -getSectorAngle(itemCount) / 2
}

function normalizeAngle(deg) {
  const normalized = deg % 360
  return normalized < 0 ? normalized + 360 : normalized
}

function getTargetRotationDeg(index, itemCount) {
  return normalizeAngle(-getSectorCenterDeg(index, itemCount))
}

export {
  getDividerDeg,
  getGradientOffsetDeg,
  getSectorAngle,
  getSectorCenterDeg,
  getSectorEndDeg,
  getSectorStartDeg,
  getTargetRotationDeg,
  normalizeAngle
}
