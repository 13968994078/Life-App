<template>
  <view class="page">
      <view class="card hero">
        <view class="hero-topline">
          <view class="hero-kicker editorial-kicker">
            <brand-icon name="spark" :size="26" />
            奇幻妙妙屋特刊
          </view>
          <view class="hero-side-note">暖调特刊 · 吃饭与节律</view>
        </view>
      <view class="hero-title">把今天的吃饭和早起，排成一张更顺手的封面。</view>
      <view class="hero-subtitle">当日常决定不再磨蹭，生活会自己往前走一点。首页只留下最该先看见的那几件事。</view>
      <view class="hero-stats">
        <view class="hero-stat">
          <view class="hero-stat-value">{{ heroFoodCount }}</view>
          <view class="hero-stat-label">当前美食数</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat-value">{{ heroStreakDays }}</view>
          <view class="hero-stat-label">连续签到</view>
        </view>
      </view>
    </view>

    <view v-if="isLoggedIn" class="summary-grid feature-lead-grid">
      <view class="card summary-card warm-card">
        <view class="summary-label">今日抽取</view>
        <view class="summary-value">{{ latestFoodName }}</view>
        <view class="summary-desc">{{ latestFoodTime }}</view>
      </view>
      <view class="card summary-card cool-card">
        <view class="summary-label">今日签到</view>
        <view class="summary-value">{{ todayCheckinText }}</view>
        <view class="summary-desc">{{ todayCheckinDesc }}</view>
      </view>
    </view>

    <view v-else class="card guest-card">
      <view class="section-title section-no-margin">先登录再开始</view>
      <view class="section-subtext guest-tip">登录后首页会展示你的抽取记录、签到状态和个人数据摘要。</view>
      <navigator class="primary-btn" url="/pages/login/index">前往登录</navigator>
    </view>

    <view class="section-head">
      <view class="editorial-kicker section-kicker">
        <brand-icon name="home" :size="26" />
        Daily Features
      </view>
      <view class="section-title">开始今天的选择</view>
      <view class="section-subtext">先吃得开心，再把作息慢慢拉回来。</view>
    </view>

    <view class="grid">
      <navigator class="card nav-card editorial-nav-card" url="/pages/food/index" open-type="switchTab">
        <brand-icon name="food" class="nav-icon-image" :size="88" />
        <view class="nav-title">随机美食</view>
        <view class="nav-desc">用转盘快速决定今天想吃什么，还能自己维护美食池。</view>
        <view class="nav-footer">
          <view class="soft-chip">转一转</view>
          <view class="nav-arrow">></view>
        </view>
      </navigator>
      <navigator class="card nav-card editorial-nav-card" url="/pages/checkin/index" open-type="switchTab">
        <brand-icon name="checkin" class="nav-icon-image" :size="88" />
        <view class="nav-title">起床签到</view>
        <view class="nav-desc">记录今天起床打卡时间，顺手看看连续签到有没有断。</view>
        <view class="nav-footer">
          <view class="soft-chip">去打卡</view>
          <view class="nav-arrow">></view>
        </view>
      </navigator>
      <navigator class="card nav-card editorial-nav-card" url="/pages/mine/index" open-type="switchTab">
        <brand-icon name="mine" class="nav-icon-image" :size="88" />
        <view class="nav-title">我的设置</view>
        <view class="nav-desc">查看账号状态、调整起床目标时间，并管理个人配置。</view>
        <view class="nav-footer">
          <view class="soft-chip">去设置</view>
          <view class="nav-arrow">></view>
        </view>
      </navigator>
    </view>

    <view v-if="isLoggedIn" class="card recent-card">
      <view class="recent-head">
        <view>
          <view class="editorial-kicker recent-kicker">
            <brand-icon name="history" :size="26" />
            Recent Briefing
          </view>
          <view class="section-title section-no-margin">最近动态</view>
          <view class="section-subtext recent-tip">首页只放最关键的两条，完整记录分别在功能页里看。</view>
        </view>
        <navigator class="recent-link editorial-link" url="/pages/food/history">更多记录</navigator>
      </view>
      <view v-if="latestFoodRecord" class="recent-row">
        <view class="recent-dot warm-dot"></view>
        <view class="recent-main">
          <view class="recent-title">最近抽到 {{ latestFoodRecord.foodName }}</view>
          <view class="recent-meta">{{ formatDateTime(latestFoodRecord.createdAt) }}</view>
        </view>
      </view>
      <view v-if="todayCheckin" class="recent-row">
        <view class="recent-dot cool-dot"></view>
        <view class="recent-main">
          <view class="recent-title">今天已签到</view>
          <view class="recent-meta">{{ formatDateTime(todayCheckin.checkTime) }} · {{ todayCheckin.status === 'ON_TIME' ? '准时' : '迟到' }}</view>
        </view>
      </view>
      <view v-if="!latestFoodRecord && !todayCheckin" class="empty-text">还没有新的动态，先去抽一次或签到一次。</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { getFoodHistory, getFoodList } from '../../api/food'
import { getTodayCheckin, getCheckinStatistics } from '../../api/checkin'
import { getToken } from '../../utils/auth'

export default {
  components: {
    BrandIcon
  },
  data() {
    return {
      foods: [],
      history: [],
      todayCheckin: null,
      statistics: {
        streakDays: 0
      }
    }
  },
  computed: {
    isLoggedIn() {
      return !!getToken()
    },
    heroFoodCount() {
      return this.isLoggedIn ? this.foods.length : 2
    },
    heroStreakDays() {
      return this.isLoggedIn ? `${this.statistics.streakDays || 0}天` : 'MVP'
    },
    latestFoodRecord() {
      return this.history.length ? this.history[0] : null
    },
    latestFoodName() {
      return this.latestFoodRecord ? this.latestFoodRecord.foodName : '还没抽取'
    },
    latestFoodTime() {
      return this.latestFoodRecord ? this.formatDateTime(this.latestFoodRecord.createdAt) : '去随机美食页转一次试试'
    },
    todayCheckinText() {
      if (!this.todayCheckin) {
        return '未签到'
      }
      return this.todayCheckin.status === 'ON_TIME' ? '已准时' : '已签到'
    },
    todayCheckinDesc() {
      return this.todayCheckin ? this.formatDateTime(this.todayCheckin.checkTime) : '还没开始今天的打卡'
    }
  },
  onShow() {
    if (!this.isLoggedIn) {
      this.resetOverview()
      return
    }
    this.loadOverview()
  },
  methods: {
    async loadOverview() {
      try {
        const [foods, history, todayCheckin, statistics] = await Promise.all([
          getFoodList('', 'PUBLIC_FIRST'),
          getFoodHistory(),
          getTodayCheckin(),
          getCheckinStatistics()
        ])
        this.foods = foods || []
        this.history = history || []
        this.todayCheckin = todayCheckin || null
        this.statistics = statistics || { streakDays: 0 }
      } catch (error) {
        this.resetOverview()
      }
    },
    resetOverview() {
      this.foods = []
      this.history = []
      this.todayCheckin = null
      this.statistics = { streakDays: 0 }
    },
    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return String(value).replace('T', ' ')
    }
  }
}
</script>

<style scoped>
.hero {
  background:
    radial-gradient(circle at top right, rgba(251, 236, 212, 0.42), transparent 24%),
    linear-gradient(135deg, #8d4f36, #c9804a 58%, #f0c37c);
  color: #fffdf8;
  margin-bottom: 28rpx;
  padding-bottom: 34rpx;
}

.hero-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.hero-kicker {
  background: rgba(255, 250, 244, 0.18);
  color: #fff7ee;
  border-color: rgba(255, 247, 238, 0.22);
}

.hero-side-note {
  max-width: 240rpx;
  font-size: 22rpx;
  line-height: 1.6;
  text-align: right;
  color: rgba(255, 248, 241, 0.82);
}

.hero-title {
  font-size: 54rpx;
  font-weight: 700;
  line-height: 1.24;
  margin-bottom: 18rpx;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.hero-subtitle {
  font-size: 27rpx;
  opacity: 0.95;
  line-height: 1.78;
  max-width: 620rpx;
}

.hero-stats {
  display: flex;
  gap: 18rpx;
  margin-top: 30rpx;
}

.hero-stat {
  flex: 1;
  padding: 22rpx 20rpx;
  border-radius: 24rpx;
  background: rgba(255, 248, 241, 0.14);
  border: 1rpx solid rgba(255, 245, 236, 0.16);
}

.hero-stat-value {
  font-size: 38rpx;
  font-weight: 700;
}

.hero-stat-label {
  margin-top: 8rpx;
  font-size: 22rpx;
  opacity: 0.84;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
  margin-bottom: 28rpx;
}

.summary-card {
  min-width: 0;
  padding-top: 34rpx;
}

.warm-card {
  background: linear-gradient(180deg, rgba(255, 247, 238, 0.98), rgba(255, 252, 247, 0.98));
}

.cool-card {
  background: linear-gradient(180deg, rgba(242, 246, 255, 0.98), rgba(251, 253, 255, 0.98));
}

.summary-label {
  color: #9b7758;
  font-size: 22rpx;
  letter-spacing: 2rpx;
  text-transform: uppercase;
}

.summary-value {
  margin-top: 12rpx;
  color: #2c201b;
  font-size: 36rpx;
  font-weight: 700;
  line-height: 1.32;
  word-break: break-all;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.summary-desc {
  margin-top: 12rpx;
  color: #78665c;
  font-size: 22rpx;
  line-height: 1.68;
}

.guest-card {
  margin-bottom: 28rpx;
}

.guest-tip {
  margin: 10rpx 0 24rpx;
}

.section-head {
  margin-bottom: 20rpx;
}

.section-kicker {
  margin-bottom: 14rpx;
}

.grid {
  display: grid;
  gap: 20rpx;
}

.nav-card {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.editorial-nav-card {
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.6), transparent 22%),
    linear-gradient(180deg, rgba(255, 252, 247, 0.98), rgba(255, 247, 239, 0.94));
  padding-top: 34rpx;
}

.nav-icon-image {
  margin-bottom: 2rpx;
}

.nav-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #281d18;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.nav-desc {
  color: #756159;
  line-height: 1.76;
}

.nav-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10rpx;
}

.nav-arrow {
  color: #a48f80;
  font-size: 32rpx;
}

.recent-card {
  margin-top: 24rpx;
}

.recent-kicker {
  margin-bottom: 14rpx;
}

.recent-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.recent-tip {
  margin-top: 10rpx;
}

.recent-link {
  flex-shrink: 0;
}

.recent-row {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
  padding-top: 24rpx;
}

.recent-dot {
  width: 20rpx;
  height: 20rpx;
  margin-top: 10rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.warm-dot {
  background: #d08742;
}

.cool-dot {
  background: #6e8cf7;
}

.recent-main {
  min-width: 0;
  flex: 1;
}

.recent-title {
  color: #2c201b;
  font-size: 30rpx;
  font-weight: 700;
}

.recent-meta {
  margin-top: 8rpx;
  color: #756159;
  font-size: 24rpx;
  line-height: 1.68;
}
</style>
