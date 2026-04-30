<script>
export default {
  onLaunch() {},
  onShow() {},
  onHide() {}
}
</script>

<style lang="scss">
@import './uni.scss';

page {
  background: $page-bg-color;
  color: $text-main;
  font-size: 28rpx;
  font-family: 'Avenir Next', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.page {
  min-height: 100vh;
  padding: 28rpx 24rpx 48rpx;
  box-sizing: border-box;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.72), transparent 24%),
    linear-gradient(180deg, rgba(255, 251, 246, 0.98), rgba(247, 240, 231, 0.98) 48%, rgba(242, 232, 220, 0.96));
}

.card {
  position: relative;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(255, 250, 245, 0.96));
  border-radius: $card-radius;
  padding: 28rpx;
  box-shadow: $card-shadow;
  border: 1rpx solid rgba(255, 255, 255, 0.88);
  overflow: hidden;
}

.card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  border: 1rpx solid $line-soft;
  pointer-events: none;
}

.section-title {
  font-size: 38rpx;
  font-weight: 700;
  line-height: 1.24;
  color: $ink-strong;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.section-subtext {
  margin-top: 10rpx;
  color: $text-sub;
  font-size: 24rpx;
  line-height: 1.68;
}

.section-label {
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 10rpx;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 247, 238, 0.82);
  color: $accent-plum;
  font-size: 20rpx;
  font-weight: 700;
  letter-spacing: 2rpx;
  white-space: nowrap;
  border: 1rpx solid rgba(216, 165, 90, 0.16);
}

.section-label text {
  white-space: nowrap;
}

.primary-btn {
  height: 92rpx;
  line-height: 92rpx;
  background: linear-gradient(135deg, $theme-color-deep, $theme-color 58%, $theme-accent);
  color: #fffaf3;
  border-radius: 999rpx;
  text-align: center;
  font-weight: 700;
  box-shadow: $card-shadow-strong;
  border: 1rpx solid rgba(255, 245, 235, 0.6);
}

.secondary-btn {
  min-height: 88rpx;
  padding: 0 28rpx;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999rpx;
  background: rgba(122, 77, 70, 0.08);
  color: $accent-plum;
  font-weight: 600;
  border: 1rpx solid rgba(122, 77, 70, 0.08);
}

.soft-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10rpx 20rpx;
  border-radius: 999rpx;
  background: rgba(216, 165, 90, 0.12);
  color: $accent-plum;
  font-size: 22rpx;
  font-weight: 600;
  border: 1rpx solid rgba(216, 165, 90, 0.18);
}

.input,
.picker-surface {
  min-height: 84rpx;
  padding: 0 24rpx;
  display: flex;
  align-items: center;
  background: linear-gradient(180deg, rgba(255, 250, 244, 0.98), rgba(250, 239, 227, 0.98));
  border-radius: 20rpx;
  color: #584840;
  border: 1rpx solid rgba(148, 118, 91, 0.14);
}

.input {
  box-sizing: border-box;
}

.empty-text {
  padding: 24rpx 0 6rpx;
  text-align: center;
  color: $ink-muted;
  font-size: 24rpx;
  line-height: 1.6;
}

.disabled {
  opacity: 0.62;
}
</style>
