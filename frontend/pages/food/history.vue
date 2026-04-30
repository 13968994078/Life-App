<template>
  <view class="page">
    <view class="card history-banner archive-stage">
      <view class="editorial-kicker">饭点档案</view>
      <view class="banner-title">抽取记录</view>
      <view class="banner-subtitle">每一次转盘结果，都像菜单页边的一行小注。</view>
      <navigator class="back-link editorial-link" url="/pages/food/index">回到美食页</navigator>
    </view>

    <view class="card summary-card archive-summary-strip" v-if="history.length">
      <view class="summary-item">
        <view class="summary-value">{{ history.length }}</view>
        <view class="summary-label">总记录</view>
      </view>
      <view class="summary-item">
        <view class="summary-value text-sm">{{ latestHistoryTime }}</view>
        <view class="summary-label">最近一次</view>
      </view>
      <view class="summary-item">
        <view class="summary-value">{{ topFoodName }}</view>
        <view class="summary-label">出现最多</view>
      </view>
    </view>

    <view class="card history-card archive-list-card">
      <view class="editorial-kicker list-kicker">页边清单</view>
      <view class="section-title section-no-margin">最近 20 条</view>
      <view class="section-subtext list-tip">饭点来过，就会在这里留下一行。</view>
      <view v-for="group in groupedHistory" :key="group.label" class="history-group">
        <view class="group-title">{{ group.label }}</view>
        <view v-for="item in group.items" :key="item.id" class="history-row">
          <view class="history-main">
            <view class="food-name">{{ item.foodName }}</view>
            <view class="food-meta">{{ formatDateTime(item.createdAt) }}</view>
          </view>
          <view class="history-index">{{ itemIndexLabel(item) }}</view>
        </view>
      </view>
      <view v-if="!history.length" class="empty-text">还没有抽取记录，先回到美食页转一次。</view>
    </view>
  </view>
</template>

<script>
import { getFoodHistory } from '../../api/food'
import { getToken } from '../../utils/auth'

export default {
  data() {
    return {
      history: []
    }
  },
  computed: {
    groupedHistory() {
      const groups = []
      this.history.forEach((item) => {
        const label = this.getGroupLabel(item.createdAt)
        const existingGroup = groups.find((group) => group.label === label)
        if (existingGroup) {
          existingGroup.items.push(item)
          return
        }

        groups.push({
          label,
          items: [item]
        })
      })
      return groups
    },
    latestHistoryTime() {
      return this.history.length ? this.formatDateTime(this.history[0].createdAt) : '--'
    },
    topFoodName() {
      if (!this.history.length) {
        return '--'
      }

      const counts = {}
      this.history.forEach((item) => {
        counts[item.foodName] = (counts[item.foodName] || 0) + 1
      })

      const sortedNames = Object.keys(counts).sort((left, right) => counts[right] - counts[left] || left.localeCompare(right))
      const highestCount = counts[sortedNames[0]]
      const topNames = sortedNames.filter((name) => counts[name] === highestCount)
      return topNames.length === 1 ? topNames[0] : `${topNames[0]} 等 ${topNames.length} 项`
    }
  },
  onShow() {
    if (!this.ensureLoggedIn()) {
      return
    }
    this.loadHistory()
  },
  methods: {
    ensureLoggedIn() {
      if (getToken()) {
        return true
      }

      uni.navigateTo({ url: `/pages/login/index?redirect=${encodeURIComponent('/pages/food/history')}` })
      return false
    },
    async loadHistory() {
      this.history = await getFoodHistory()
    },
    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return value.replace('T', ' ')
    },
    getGroupLabel(value) {
      if (!value) {
        return '更早'
      }

      const dateText = value.split('T')[0]
      const today = new Date()
      const todayText = `${today.getFullYear()}-${`${today.getMonth() + 1}`.padStart(2, '0')}-${`${today.getDate()}`.padStart(2, '0')}`
      const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000)
      const yesterdayText = `${yesterday.getFullYear()}-${`${yesterday.getMonth() + 1}`.padStart(2, '0')}-${`${yesterday.getDate()}`.padStart(2, '0')}`

      if (dateText === todayText) {
        return '今天'
      }
      if (dateText === yesterdayText) {
        return '昨天'
      }
      return dateText
    },
    itemIndexLabel(item) {
      const index = this.history.findIndex((historyItem) => historyItem.id === item.id)
      return `${index + 1}`.padStart(2, '0')
    }
  }
}
</script>

<style scoped>
.history-banner {
  margin-bottom: 24rpx;
  background:
    radial-gradient(circle at top right, rgba(255, 240, 220, 0.24), transparent 24%),
    linear-gradient(135deg, #5b4032, #8b5f45 56%, #d5a46b);
  color: #fffdf8;
}

.banner-title {
  margin-top: 18rpx;
  font-size: 48rpx;
  font-weight: 700;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.banner-subtitle {
  margin-top: 14rpx;
  font-size: 26rpx;
  line-height: 1.78;
  opacity: 0.94;
}

.back-link {
  margin-top: 18rpx;
  color: #fff7ef;
}

.history-card {
  display: flex;
  flex-direction: column;
}

.summary-card {
  margin-bottom: 24rpx;
  display: flex;
  gap: 14rpx;
}

.archive-summary-strip,
.archive-list-card {
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.58), transparent 18%),
    linear-gradient(180deg, rgba(255, 252, 247, 0.98), rgba(255, 247, 239, 0.95));
}

.summary-item {
  flex: 1;
  min-width: 0;
  padding: 26rpx 18rpx;
  border-radius: 24rpx;
  background: linear-gradient(180deg, rgba(255, 248, 240, 0.98), rgba(255, 252, 248, 0.98));
  text-align: center;
  border: 1rpx solid rgba(118, 86, 66, 0.08);
}

.summary-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #2f221d;
  word-break: break-all;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.text-sm {
  font-size: 22rpx;
  line-height: 1.5;
}

.summary-label {
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #9c7d68;
}

.list-kicker {
  margin-bottom: 14rpx;
}

.history-group {
  display: flex;
  flex-direction: column;
}

.group-title {
  margin-top: 16rpx;
  padding: 12rpx 0 4rpx;
  font-size: 24rpx;
  font-weight: 700;
  color: #8f6040;
}

.section-no-margin {
  margin-bottom: 0;
}

.list-tip {
  margin-top: 10rpx;
  margin-bottom: 10rpx;
  color: #7d675c;
  font-size: 24rpx;
  line-height: 1.7;
}

.history-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid rgba(118, 86, 66, 0.12);
}

.history-row:last-child {
  border-bottom: 0;
}

.history-main {
  flex: 1;
  min-width: 0;
}

.food-name {
  font-size: 30rpx;
  font-weight: 700;
  color: #2e211c;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.food-meta {
  margin-top: 8rpx;
  color: #7d675c;
  font-size: 24rpx;
}

.history-index {
  width: 64rpx;
  height: 64rpx;
  border-radius: 18rpx;
  background: rgba(215, 165, 90, 0.14);
  color: #7a4d46;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 700;
  border: 1rpx solid rgba(215, 165, 90, 0.16);
}
</style>
