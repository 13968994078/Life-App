<template>
  <view class="wheel-card card">
    <view class="wheel-head">
      <view>
        <view class="wheel-title">本期转盘</view>
        <view class="wheel-subtitle">把犹豫交给这一圈颜色。</view>
      </view>
      <view class="soft-chip">{{ spinning ? '正在转' : '今日候选' }}</view>
    </view>

    <view class="wheel-shell" :style="wheelShellStyle">
      <view class="wheel" :style="wheelStyle">
        <view class="wheel-surface" :style="surfaceStyle"></view>
        <view
          v-for="(item, index) in normalizedItems"
          :key="`divider-${index}`"
          class="wheel-divider"
          :style="getDividerStyle(index)"
        ></view>
        <view
          v-for="(item, index) in normalizedItems"
          :key="`${item.label}-${index}`"
          class="wheel-label-anchor"
          :style="getLabelAnchorStyle(index)"
        >
          <view class="wheel-label-wrap" :style="getLabelWrapStyle(index)">
            <view class="wheel-label" :class="{ active: highlightedItem === item.label }" :style="getLabelStyle(item.label)">
              {{ getDisplayLabel(item.label) }}
            </view>
          </view>
        </view>
      </view>
      <view class="wheel-center" :style="{ width: `${wheelFrameLayout.centerSize}rpx`, height: `${wheelFrameLayout.centerSize}rpx`, fontSize: `${wheelFrameLayout.centerFontSize}rpx` }">抽</view>
      <view class="wheel-pointer" :style="{ top: `${wheelFrameLayout.pointerTop}rpx`, borderLeftWidth: `${wheelFrameLayout.pointerSide}rpx`, borderRightWidth: `${wheelFrameLayout.pointerSide}rpx`, borderBottomWidth: `${wheelFrameLayout.pointerHeight}rpx` }"></view>
    </view>

    <view class="result-card">
      <view class="result-label">抽取结果</view>
      <view class="wheel-result">{{ resultText }}</view>
    </view>

    <view class="primary-btn" :class="{ disabled: spinning }" @tap="$emit('spin')">
      {{ spinning ? '转盘转着...' : '开始转' }}
    </view>
    <view class="wheel-tip">分类越贴近当下，这一转越有参考感。</view>
  </view>
</template>

<script>
import { getDividerDeg, getGradientOffsetDeg, getSectorAngle, getSectorCenterDeg } from '../utils/wheel-geometry'
import { getWheelFrameLayout } from '../utils/wheel-frame-layout'
import { getWheelLabelLayout } from '../utils/wheel-label-layout'
import { getWheelLabelWrapRotationDeg } from '../utils/wheel-label-orientation'

const DEFAULT_COLORS = ['#ffb703', '#fb8500', '#3a86ff', '#8338ec', '#ff006e', '#06d6a0', '#8ecae6', '#219ebc']

export default {
  props: {
    items: {
      type: Array,
      default: () => []
    },
    result: {
      type: String,
      default: ''
    },
    rotationDeg: {
      type: Number,
      default: 0
    },
    spinning: {
      type: Boolean,
      default: false
    },
    highlightedItem: {
      type: String,
      default: ''
    }
  },
  computed: {
    normalizedItems() {
      const rawItems = this.items.length ? this.items : ['早餐', '午餐', '晚餐', '夜宵']
      return rawItems.map((item, index) => {
        if (typeof item === 'object' && item) {
          return {
            label: item.label || item.name || `选项${index + 1}`,
            color: item.color || DEFAULT_COLORS[index % DEFAULT_COLORS.length]
          }
        }

        return {
          label: String(item),
          color: DEFAULT_COLORS[index % DEFAULT_COLORS.length]
        }
      })
    },
    itemCount() {
      return this.normalizedItems.length || 4
    },
    sectorAngle() {
      return getSectorAngle(this.itemCount)
    },
    resultText() {
      return this.result || '点下面按钮，让转盘开个头'
    },
    wheelStyle() {
      return {
        transform: `rotate(${this.rotationDeg}deg)`,
        transitionDuration: this.spinning ? '4200ms' : '0ms'
      }
    },
    wheelShellStyle() {
      return {
        width: `${this.wheelFrameLayout.shellSize}rpx`,
        height: `${this.wheelFrameLayout.shellSize}rpx`
      }
    },
    surfaceStyle() {
      return {
        background: this.buildConicGradient()
      }
    },
    wheelFrameLayout() {
      return getWheelFrameLayout()
    },
    labelLayout() {
      return getWheelLabelLayout(this.itemCount)
    }
  },
  methods: {
    buildConicGradient() {
      const stops = this.normalizedItems.map((item, index) => {
        const start = index * this.sectorAngle
        const end = (index + 1) * this.sectorAngle
        return `${item.color} ${start}deg ${end}deg`
      })
      return `conic-gradient(from ${getGradientOffsetDeg(this.itemCount)}deg, ${stops.join(', ')})`
    },
    getLabelAnchorStyle(index) {
      const angle = getSectorCenterDeg(index, this.itemCount)
      return {
        transform: `translate(-50%, -50%) rotate(${angle}deg) translateY(-${this.labelLayout.labelRadius}rpx)`
      }
    },
    getDividerStyle(index) {
      return {
        top: `${this.wheelFrameLayout.dividerTop}rpx`,
        height: `${this.wheelFrameLayout.dividerHeight}rpx`,
        transformOrigin: `center ${this.wheelFrameLayout.dividerOrigin}rpx`,
        transform: `translateX(-50%) rotate(${getDividerDeg(index, this.itemCount)}deg)`
      }
    },
    getLabelWrapStyle(index) {
      const angle = getSectorCenterDeg(index, this.itemCount)
      return {
        transform: `rotate(${getWheelLabelWrapRotationDeg(angle, this.rotationDeg)}deg)`
      }
    },
    getLabelStyle(label) {
      const text = this.getDisplayLabel(label)
      const fontSize = this.getFittedFontSize(text)
      return {
        width: `${this.labelLayout.labelWidth}rpx`,
        fontSize: `${fontSize}rpx`
      }
    },
    getDisplayLabel(label) {
      const text = String(label || '').trim()
      if (!text) {
        return ''
      }

      const maxChars = this.labelLayout.maxChars
      if (text.length <= maxChars) {
        return text
      }
      return `${text.slice(0, maxChars - 1)}…`
    },
    getFittedFontSize(text) {
      const length = Array.from(text).length
      const baseFontSize = this.labelLayout.labelFontSize
      if (length <= 6) return baseFontSize
      if (length <= 8) return Math.max(14, baseFontSize - 2)
      return Math.max(12, baseFontSize - 4)
    }
  }
}
</script>

<style scoped>
.wheel-card {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  overflow: hidden;
}

.wheel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.wheel-title {
  font-size: 36rpx;
  font-weight: 700;
}

.wheel-subtitle {
  margin-top: 8rpx;
  color: #8a7b70;
  font-size: 24rpx;
}

.wheel-shell {
  position: relative;
  margin: 0 auto;
}

.wheel {
  width: 100%;
  height: 100%;
  position: relative;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: inset 0 0 0 16rpx rgba(255, 247, 231, 0.34), 0 24rpx 48rpx rgba(255, 153, 0, 0.2);
  transition-property: transform;
  transition-timing-function: cubic-bezier(0.15, 0.85, 0.12, 1);
}

.wheel::before {
  content: '';
  position: absolute;
  inset: 8rpx;
  border-radius: 50%;
  border: 10rpx solid rgba(255, 248, 227, 0.8);
  box-shadow: inset 0 0 0 4rpx rgba(223, 111, 45, 0.16);
  z-index: 4;
  pointer-events: none;
}

.wheel::after {
  content: '';
  position: absolute;
  inset: 28rpx;
  border-radius: 50%;
  border: 2rpx solid rgba(255, 248, 227, 0.55);
  z-index: 3;
  pointer-events: none;
}

.wheel-surface {
  position: absolute;
  inset: 0;
  border-radius: 50%;
}

.wheel-label-anchor {
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 5;
}

.wheel-divider {
  position: absolute;
  left: 50%;
  width: 4rpx;
  border-radius: 999rpx;
  background: rgba(255, 250, 240, 0.92);
  box-shadow: 0 0 0 1rpx rgba(255, 214, 176, 0.35);
  z-index: 4;
}

.wheel-label-wrap {
  transform-origin: center;
}

.wheel-label {
  color: #fff7ef;
  font-weight: 700;
  line-height: 1.15;
  text-align: center;
  box-sizing: border-box;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 2rpx 6rpx rgba(74, 37, 18, 0.32);
  letter-spacing: 1rpx;
}

.active {
  color: #ffffff;
  text-shadow: 0 0 12rpx rgba(255, 255, 255, 0.4), 0 2rpx 8rpx rgba(74, 37, 18, 0.36);
}

.wheel-center {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  background: linear-gradient(135deg, #ff7a18, #ff5b3d);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  box-shadow: 0 16rpx 34rpx rgba(255, 102, 0, 0.28);
  z-index: 6;
}

.wheel-pointer {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left-style: solid;
  border-left-color: transparent;
  border-right-style: solid;
  border-right-color: transparent;
  border-bottom-style: solid;
  border-bottom: 46rpx solid #ff5b3d;
  filter: drop-shadow(0 8rpx 12rpx rgba(190, 72, 25, 0.2));
}

.result-card {
  padding: 20rpx 22rpx;
  border-radius: 20rpx;
  background: linear-gradient(180deg, rgba(255, 240, 229, 0.95), rgba(255, 251, 247, 0.95));
}

.result-label {
  font-size: 22rpx;
  color: #b16c41;
  margin-bottom: 8rpx;
}

.wheel-result {
  text-align: center;
  font-size: 30rpx;
  color: #5b463d;
  font-weight: 700;
}

.wheel-tip {
  text-align: center;
  color: #93857b;
  font-size: 22rpx;
  line-height: 1.7;
}

.disabled {
  opacity: 0.76;
}
</style>
