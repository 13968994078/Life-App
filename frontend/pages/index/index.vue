<template>
  <view class="page home-page">
    <view class="card home-hero">
      <view class="hero-topline">
        <view class="section-label">
          <brand-icon name="quote" :size="24" />
          <text>今日短笺</text>
        </view>
        <view class="hero-source">Good Luck~</view>
      </view>
      <view class="hero-title">{{ content.homeHero.title }}</view>
      <view class="hero-subtitle">{{ content.homeHero.subtitle }}</view>
      <view class="quote-card">
        <view class="quote-text">{{ content.quote.text }}</view>
      </view>
      <view class="hero-metrics">
        <view class="hero-metric">
          <view class="hero-metric-label">美食收录</view>
          <view class="hero-metric-value">{{ heroFoodCount }}</view>
        </view>
        <view class="hero-metric">
          <view class="hero-metric-label">连续签到</view>
          <view class="hero-metric-value">{{ heroStreakDays }}</view>
        </view>
      </view>
    </view>

    <view v-if="isLoggedIn" class="summary-strip">
      <view class="card summary-card">
        <view class="summary-label">最近抽取</view>
        <view class="summary-value">{{ latestFoodName }}</view>
        <view class="summary-desc">{{ latestFoodTime }}</view>
      </view>
      <view class="card summary-card cool-card">
        <view class="summary-label">今天签到</view>
        <view class="summary-value">{{ todayCheckinText }}</view>
        <view class="summary-desc">{{ todayCheckinDesc }}</view>
      </view>
    </view>

    <view v-else class="card guest-card">
      <view class="section-title">先登录，翻开你的这一页</view>
      <view class="section-subtext">抽取、签到和提醒会回到自己的版面。</view>
      <navigator class="primary-btn" url="/pages/login/index">去登录</navigator>
    </view>

    <view class="card home-actions">
      <view class="section-label">
        <brand-icon name="tea" :size="24" />
        <text>本期小事</text>
      </view>
      <view class="section-title actions-title">从两件小事开始</view>
      <view class="action-grid">
        <navigator class="action-card" url="/pages/food/index" open-type="switchTab">
          <brand-icon name="food" :size="54" />
          <view class="action-card-title">随机美食</view>
          <view class="action-card-desc">饭点没主意，就让转盘递个选项。</view>
        </navigator>
        <navigator class="action-card" url="/pages/checkin/index" open-type="switchTab">
          <brand-icon name="checkin" :size="54" />
          <view class="action-card-title">起床签到</view>
          <view class="action-card-desc">把起床时间记一笔，给清晨留个坐标。</view>
        </navigator>
        <navigator class="action-card" url="/pages/mine/index" open-type="switchTab">
          <brand-icon name="mine" :size="54" />
          <view class="action-card-title">我的主页</view>
          <view class="action-card-desc">状态、提醒和资料，都收进这一页。</view>
        </navigator>
      </view>
    </view>

    <view class="card home-tools">
      <view class="section-label">
        <brand-icon name="stats" :size="24" />
        <text>生活工具</text>
      </view>
      <view class="section-title tools-title">把常用计算放在手边</view>
      <view class="tool-grid">
        <navigator class="tool-card tax-tool-card" url="/pages/tools/tax-calculator">
          <view class="tool-icon">
            <brand-icon name="target" :size="46" />
          </view>
          <view class="tool-main">
            <view class="tool-title">个税计算器</view>
            <view class="tool-desc">按工资薪金累计预扣预缴，估一估本月个税和到手收入。</view>
          </view>
          <view class="tool-arrow">›</view>
        </navigator>
        <navigator class="tool-card loan-tool-card" url="/pages/tools/loan-calculator">
          <view class="tool-icon loan-tool-icon">
            <brand-icon name="home" :size="46" />
          </view>
          <view class="tool-main">
            <view class="tool-title">贷款计算器</view>
            <view class="tool-desc">按等额本息或等额本金，估一估月供、利息和还款总额。</view>
          </view>
          <view class="tool-arrow">›</view>
        </navigator>
      </view>
    </view>

    <view v-if="isLoggedIn" class="card recent-card">
      <view class="recent-head">
        <view>
          <view class="section-label">
            <brand-icon name="history" :size="24" />
            <text>页边记录</text>
          </view>
          <view class="section-title recent-title-main">最近两笔</view>
        </view>
        <navigator class="recent-link" url="/pages/food/history">更多记录</navigator>
      </view>
      <view v-if="latestFoodRecord" class="recent-row">
        <view class="recent-dot warm-dot"></view>
        <view class="recent-body">
          <view class="recent-title">上次吃到 {{ latestFoodRecord.foodName }}</view>
          <view class="recent-meta">{{ formatDateTime(latestFoodRecord.createdAt) }}</view>
        </view>
      </view>
      <view v-if="todayCheckin" class="recent-row">
        <view class="recent-dot cool-dot"></view>
        <view class="recent-body">
          <view class="recent-title">今天已签到</view>
          <view class="recent-meta">{{ formatDateTime(todayCheckin.checkTime) }} · {{ todayCheckin.status === 'ON_TIME' ? '准时' : '迟到' }}</view>
        </view>
      </view>
      <view v-if="!latestFoodRecord && !todayCheckin" class="empty-text">今天还没有新记录，可以先转一餐或签个到。</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { getTodayCheckin, getCheckinStatistics } from '../../api/checkin'
import { getHomeContent } from '../../api/content'
import { getFoodHistory, getFoodList } from '../../api/food'
import { getToken } from '../../utils/auth'

function createDefaultContent() {
  return {
    quote: {
      text: '把日子翻到今天这一页，先吃好一顿。',
      source: '今日短笺'
    },
    homeHero: {
      title: '今天这一页，从饭点和作息开始',
      subtitle: '先吃稳一餐，再记下一次起床时间，日子就有了线索。'
    }
  }
}

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
      },
      content: createDefaultContent()
    }
  },
  computed: {
    isLoggedIn() {
      return !!getToken()
    },
    heroFoodCount() {
      return this.isLoggedIn ? this.foods.length : '未登录'
    },
    heroStreakDays() {
      return this.isLoggedIn ? `${this.statistics.streakDays || 0}天` : '待开启'
    },
    latestFoodRecord() {
      return this.history.length ? this.history[0] : null
    },
    latestFoodName() {
      return this.latestFoodRecord ? this.latestFoodRecord.foodName : '还没抽取'
    },
    latestFoodTime() {
      return this.latestFoodRecord ? this.formatDateTime(this.latestFoodRecord.createdAt) : '去美食页转一次'
    },
    todayCheckinText() {
      if (!this.todayCheckin) {
        return '未签到'
      }
      return this.todayCheckin.status === 'ON_TIME' ? '已准时' : '已签到'
    },
    todayCheckinDesc() {
      return this.todayCheckin ? this.formatDateTime(this.todayCheckin.checkTime) : '今天还没签到'
    }
  },
  onShow() {
    this.loadPageData()
  },
  methods: {
    async loadPageData() {
      const contentPromise = getHomeContent().catch(() => createDefaultContent())
      if (!this.isLoggedIn) {
        this.content = await contentPromise
        this.resetOverview()
        return
      }

      try {
        const [content, foods, history, todayCheckin, statistics] = await Promise.all([
          contentPromise,
          getFoodList('', 'PUBLIC_FIRST'),
          getFoodHistory(),
          getTodayCheckin(),
          getCheckinStatistics()
        ])
        this.content = content || createDefaultContent()
        this.foods = foods || []
        this.history = history || []
        this.todayCheckin = todayCheckin || null
        this.statistics = statistics || { streakDays: 0 }
      } catch (error) {
        this.content = await contentPromise
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
.home-hero {
  background: linear-gradient(135deg, #6d4336, #a96645 58%, #e0b072);
  color: #fffaf4;
}

.hero-topline,
.recent-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.hero-source {
  font-size: 22rpx;
  color: rgba(255, 246, 236, 0.82);
}

.hero-title {
  margin-top: 20rpx;
  font-size: 52rpx;
  line-height: 1.2;
  font-weight: 700;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.hero-subtitle {
  margin-top: 16rpx;
  font-size: 26rpx;
  line-height: 1.72;
  color: rgba(255, 247, 239, 0.9);
}

.quote-card {
  margin-top: 26rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  background: rgba(255, 248, 241, 0.14);
  border: 1rpx solid rgba(255, 245, 236, 0.16);
}

.quote-mark {
  font-size: 52rpx;
  line-height: 1;
}

.quote-text {
  margin-top: 6rpx;
  font-size: 28rpx;
  line-height: 1.7;
}

.hero-metrics,
.summary-strip,
.action-grid {
  display: grid;
  gap: 18rpx;
}

.hero-metrics,
.summary-strip {
  grid-template-columns: repeat(2, 1fr);
}

.hero-metrics {
  margin-top: 24rpx;
}

.hero-metric {
  padding: 20rpx;
  border-radius: 22rpx;
  background: rgba(255, 248, 241, 0.14);
}

.hero-metric-label,
.summary-label {
  font-size: 22rpx;
  color: #271b16;
}

.hero-metric-value {
  margin-top: 10rpx;
  font-size: 34rpx;
  font-weight: 700;
}

.summary-strip,
.guest-card,
.home-actions,
.home-tools,
.recent-card {
  margin-top: 24rpx;
}

.summary-card,
.action-card {
  background: linear-gradient(180deg, rgba(255, 252, 248, 0.98), rgba(255, 246, 238, 0.96));
}

.cool-card {
  background: linear-gradient(180deg, rgba(248, 249, 255, 0.98), rgba(242, 245, 255, 0.96));
}

.summary-value {
  margin-top: 12rpx;
  color: #271b16;
  font-size: 34rpx;
  font-weight: 700;
  line-height: 1.32;
}

.summary-desc {
  margin-top: 10rpx;
  color: #7a675d;
  font-size: 22rpx;
  line-height: 1.6;
}

.guest-card .primary-btn {
  margin-top: 24rpx;
}

.actions-title,
.tools-title {
  margin-top: 18rpx;
}

.action-grid,
.tool-grid {
  margin-top: 22rpx;
}

.action-card {
  display: block;
  padding: 26rpx;
  border-radius: 26rpx;
}

.action-card-title {
  margin-top: 18rpx;
  font-size: 32rpx;
  font-weight: 700;
  color: #2b1f1b;
}

.action-card-desc {
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.65;
  color: #7d6a60;
}

.tool-grid {
  display: grid;
  gap: 18rpx;
}

.tool-card {
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, rgba(247, 251, 244, 0.98), rgba(255, 248, 235, 0.96));
}

.tax-tool-card {
  border: 1rpx solid rgba(105, 133, 102, 0.12);
}

.loan-tool-card {
  border: 1rpx solid rgba(82, 111, 128, 0.12);
  background: linear-gradient(135deg, rgba(244, 249, 251, 0.98), rgba(255, 249, 238, 0.96));
}

.tool-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 82rpx;
  height: 82rpx;
  border-radius: 24rpx;
  background: rgba(111, 138, 112, 0.12);
  flex-shrink: 0;
}

.loan-tool-icon {
  background: rgba(82, 111, 128, 0.12);
}

.tool-main {
  flex: 1;
  min-width: 0;
}

.tool-title {
  color: #2b1f1b;
  font-size: 30rpx;
  font-weight: 700;
}

.tool-desc {
  margin-top: 8rpx;
  color: #7d6a60;
  font-size: 23rpx;
  line-height: 1.55;
}

.tool-arrow {
  color: #7a4d46;
  font-size: 42rpx;
  line-height: 1;
  flex-shrink: 0;
}

.recent-link {
  color: #7a4d46;
  font-size: 24rpx;
  font-weight: 600;
}

.recent-title-main {
  margin-top: 16rpx;
}

.recent-row {
  display: flex;
  gap: 18rpx;
  padding-top: 24rpx;
}

.recent-dot {
  width: 18rpx;
  height: 18rpx;
  margin-top: 10rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.warm-dot {
  background: #d08742;
}

.cool-dot {
  background: #6985f6;
}

.recent-body {
  flex: 1;
  min-width: 0;
}

.recent-title {
  color: #2c201b;
  font-size: 28rpx;
  font-weight: 700;
}

.recent-meta {
  margin-top: 8rpx;
  color: #7d6a60;
  font-size: 22rpx;
}

@media screen and (max-width: 380px) {
  .hero-metrics,
  .summary-strip {
    grid-template-columns: 1fr;
  }
}
</style>
