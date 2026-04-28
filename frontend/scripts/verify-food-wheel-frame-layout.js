const assert = require('assert')

async function main() {
  const { getWheelFrameLayout } = await import('../utils/wheel-frame-layout.js')
  const frame = getWheelFrameLayout()

  assert(frame.shellSize >= 456, `wheel shell should be larger than before, received ${frame.shellSize}`)
  assert(frame.centerSize >= 116, `wheel center should scale up with the shell, received ${frame.centerSize}`)
  assert(frame.pointerHeight >= 52, `wheel pointer should scale up with the shell, received ${frame.pointerHeight}`)
  assert(frame.dividerTop + frame.dividerHeight === frame.shellSize / 2, 'divider should still reach the wheel center')

  console.log('food wheel frame layout verification passed')
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
