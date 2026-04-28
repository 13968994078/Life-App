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
  position: relative;
  min-height: 100vh;
  padding: 32rpx 30rpx 48rpx;
  box-sizing: border-box;
  background:
    linear-gradient(180deg, rgba(255, 250, 244, 0.98), rgba(249, 241, 232, 0.98) 46%, rgba(245, 233, 219, 0.96));
  overflow: hidden;
}

.page::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at top right, rgba(243, 195, 123, 0.2), transparent 26%),
    radial-gradient(circle at left 18% top 12%, rgba(255, 255, 255, 0.65), transparent 18%),
    radial-gradient(circle at right 18% bottom 14%, rgba(216, 165, 90, 0.1), transparent 18%);
  pointer-events: none;
  animation: page-aura-shift 18s ease-in-out infinite alternate;
}

.page::after {
  content: '';
  position: absolute;
  inset: 0;
  background:
    repeating-linear-gradient(
      115deg,
      rgba(122, 77, 54, 0.02) 0,
      rgba(122, 77, 54, 0.02) 4rpx,
      transparent 4rpx,
      transparent 22rpx
    );
  opacity: 0.55;
  pointer-events: none;
  animation: page-grain-drift 24s linear infinite;
}

.card {
  position: relative;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(255, 249, 243, 0.96));
  border-radius: $card-radius;
  padding: 30rpx;
  box-shadow: $card-shadow;
  border: 1rpx solid rgba(255, 255, 255, 0.85);
  overflow: hidden;
  animation: card-rise 520ms cubic-bezier(0.22, 1, 0.36, 1) both;
  transition: transform 220ms ease, box-shadow 220ms ease;
}

.card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  border: 1rpx solid $line-soft;
  pointer-events: none;
}

.page > .card:nth-of-type(2) {
  animation-delay: 70ms;
}

.page > .card:nth-of-type(3) {
  animation-delay: 120ms;
}

.page > .card:nth-of-type(4) {
  animation-delay: 170ms;
}

.page > .card:nth-of-type(5) {
  animation-delay: 220ms;
}

.section-title {
  font-size: 38rpx;
  font-weight: 700;
  margin-bottom: 24rpx;
  color: $ink-strong;
  line-height: 1.22;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
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
  transition: transform 180ms ease, box-shadow 180ms ease, opacity 180ms ease;
}

.soft-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10rpx 22rpx;
  border-radius: 999rpx;
  background: rgba(216, 165, 90, 0.12);
  color: $accent-plum;
  font-size: 22rpx;
  font-weight: 600;
  border: 1rpx solid rgba(216, 165, 90, 0.18);
}

.muted-text {
  color: $text-muted;
}

.editorial-kicker {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 248, 240, 0.72);
  color: $accent-plum;
  font-size: 20rpx;
  font-weight: 700;
  letter-spacing: 3rpx;
  text-transform: uppercase;
  border: 1rpx solid rgba(216, 165, 90, 0.2);
}

.editorial-link {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  color: $accent-plum;
  font-size: 24rpx;
  font-weight: 600;
  line-height: 1.7;
  transition: transform 180ms ease, opacity 180ms ease;
}

.section-subtext {
  color: $text-sub;
  font-size: 24rpx;
  line-height: 1.7;
}

.empty-text {
  color: $ink-muted;
  text-align: center;
  padding: 28rpx 0 10rpx;
}

.primary-btn:active,
.editorial-link:active {
  transform: translateY(2rpx);
}

@keyframes page-aura-shift {
  0% {
    transform: translate3d(0, 0, 0) scale(1);
  }

  100% {
    transform: translate3d(0, -10rpx, 0) scale(1.02);
  }
}

@keyframes page-grain-drift {
  0% {
    transform: translate3d(0, 0, 0);
  }

  100% {
    transform: translate3d(-8rpx, 6rpx, 0);
  }
}

@keyframes card-rise {
  0% {
    opacity: 0;
    transform: translate3d(0, 18rpx, 0);
  }

  100% {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}
</style>
