import { getSectorAngle } from './wheel-geometry.js'

function clamp(value, min, max) {
  return Math.max(min, Math.min(max, value))
}

function normalizeItemCount(itemCount) {
  const count = Number(itemCount)
  if (!Number.isFinite(count) || count <= 0) {
    return 1
  }
  return Math.max(1, Math.floor(count))
}

export function getWheelLabelLayout(itemCount) {
  const count = normalizeItemCount(itemCount)

  let labelRadius = 118
  let labelFontSize = 17
  let minWidth = 72
  let maxWidth = 90

  if (count <= 4) {
    labelRadius = 160
    labelFontSize = 24
    minWidth = 128
    maxWidth = 148
  } else if (count <= 6) {
    labelRadius = 152
    labelFontSize = 22
    minWidth = 108
    maxWidth = 124
  } else if (count <= 8) {
    labelRadius = 144
    labelFontSize = 19
    minWidth = 88
    maxWidth = 112
  } else {
    labelRadius = 126
    maxWidth = 94
  }

  const sectorAngle = getSectorAngle(count)
  const estimatedArcWidth = Math.round(labelRadius * sectorAngle * Math.PI / 180 * 0.92)
  const labelWidth = clamp(estimatedArcWidth, minWidth, maxWidth)
  const maxChars = Math.max(count <= 8 ? 6 : 5, Math.min(9, Math.floor((labelWidth + 6) / 16)))

  return {
    labelFontSize,
    labelRadius,
    labelWidth,
    maxChars
  }
}
