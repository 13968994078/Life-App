<template>
  <view class="page checkin-page">
    <view class="card checkin-hero status-hero">
      <view class="hero-topline">
        <view class="section-label">
          <brand-icon :name="today ? (today.status === 'ON_TIME' ? 'success' : 'warning') : 'time'" :size="24" />
          <text>签到页</text>
        </view>
        <view class="hero-badge" :class="today ? (today.status === 'ON_TIME' ? 'ok' : 'late') : 'idle'">{{ today ? (today.status === 'ON_TIME' ? '已准时' : '已签到') : '待签到' }}</view>
      </view>
      <view class="hero-title">{{ content.title }}</view>
      <view class="hero-subtitle">{{ content.subtitle }}</view>
      <view class="hero-strip">
        <view class="hero-strip-item">
          <view class="hero-strip-label">目标时间</view>
          <view class="hero-strip-value">{{ setting.wakeTargetTime || '--:--' }}</view>
        </view>
        <view class="hero-strip-item">
          <view class="hero-strip-label">连续</view>
          <view class="hero-strip-value">{{ statistics.streakDays || 0 }} 天</view>
        </view>
      </view>
    </view>

    <view class="card status-card">
      <view class="section-title">{{ today ? '今天已签到' : '今天还没签到' }}</view>
      <view class="section-subtext">{{ statusText }}</view>
      <view class="status-inline">
        <view class="soft-chip">目标时间 {{ setting.wakeTargetTime || '--:--' }}</view>
        <view class="soft-chip">累计 {{ statistics.totalDays || 0 }} 天</view>
      </view>
      <view class="primary-btn" @tap="handleCheckIn">{{ today ? '今天已签到' : '立即签到' }}</view>
    </view>

    <view class="card stats-card">
      <view class="section-label">
        <brand-icon name="stats" :size="24" />
        <text>清晨统计</text>
      </view>
      <view class="section-title stats-title">连续和累计，像页脚的小数字</view>
      <view class="stats-grid">
        <view class="stat-box">
          <view class="stat-value">{{ statistics.streakDays || 0 }}</view>
          <view class="stat-label">连续签到</view>
        </view>
        <view class="stat-box">
          <view class="stat-value">{{ statistics.totalDays || 0 }}</view>
          <view class="stat-label">累计签到</view>
        </view>
      </view>
    </view>

    <view class="card board-card public-board-card">
      <view class="section-label">
        <brand-icon name="board" :size="24" />
        <text>龙虎榜</text>
      </view>
      <view class="section-title board-title">签到龙虎榜</view>
      <view v-for="(item, index) in publicBoard" :key="item.userId" class="board-row">
        <view class="board-rank">{{ index + 1 }}</view>
        <view class="board-avatar-wrap">
          <image v-if="item.avatar" class="board-avatar-image" :src="item.avatar" mode="aspectFill" />
          <view v-else class="board-avatar-fallback">{{ boardAvatarLetter(item) }}</view>
        </view>
        <view class="board-main">
          <view class="board-name-row">
            <view class="board-name">{{ item.nickname || item.username }}</view>
            <view v-if="item.userId === currentUserId" class="self-badge">我</view>
          </view>
          <view class="board-meta">连续 {{ item.streakDays || 0 }} 天 · 累计 {{ item.totalDays || 0 }} 天</view>
          <view class="board-meta muted">{{ boardSubmeta(item) }}</view>
        </view>
        <view class="board-status" :class="item.todayStatus === 'ON_TIME' ? 'ok' : item.todayStatus === 'LATE' ? 'late' : 'idle'">{{ boardStatusText(item) }}</view>
      </view>
      <view v-if="!publicBoard.length" class="empty-text">还没有公开签到数据。</view>
    </view>

    <view class="card calendar-card">
      <view class="calendar-head">
        <view>
          <view class="section-label">
            <brand-icon name="calendar" :size="24" />
            <text>本月版面</text>
          </view>
          <view class="section-title calendar-title">签到日历</view>
        </view>
        <view class="month-switch">
          <view class="month-btn" @tap="changeMonth(-1)">&lt;</view>
          <view class="month-text">{{ monthTitle }}</view>
          <view class="month-btn" @tap="changeMonth(1)">&gt;</view>
        </view>
      </view>
      <view class="weekday-row">
        <view v-for="day in weekdayLabels" :key="day" class="weekday-cell">{{ day }}</view>
      </view>
      <view class="calendar-grid">
        <view
          v-for="item in calendarGrid"
          :key="item.key"
          class="calendar-cell"
          :class="{ mutedCell: !item.currentMonth, todayCell: item.isToday, checked: item.checked, lateCell: item.status === 'LATE' }"
        >
          <view class="calendar-day">{{ item.day }}</view>
          <view v-if="item.checked" class="calendar-mark">{{ item.status === 'ON_TIME' ? '准' : '迟' }}</view>
        </view>
      </view>
    </view>

    <view class="card record-card">
      <view class="section-label">
        <brand-icon name="time" :size="24" />
        <text>清晨记录</text>
      </view>
      <view class="section-title record-title">这一月的起床记录</view>
      <view v-for="item in calendar" :key="item.id" class="record-row">
        <view>
          <view class="record-date">{{ item.checkDate }}</view>
          <view class="record-time">{{ formatDateTime(item.checkTime) }}</view>
        </view>
        <view class="record-status" :class="item.status === 'ON_TIME' ? 'ok' : 'late'">{{ item.status === 'ON_TIME' ? '准时' : '迟到' }}</view>
      </view>
      <view v-if="!calendar.length" class="empty-text">{{ content.emptyTip }}</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { doCheckin, getCheckinCalendar, getCheckinStatistics, getPublicCheckinBoard, getTodayCheckin } from '../../api/checkin'
import { getHomeContent } from '../../api/content'
import { getSettings } from '../../api/settings'
import { getAuthUser, getToken } from '../../utils/auth'

function createDefaultCheckinContent() {
  return {
    title: '把清晨留一笔',
    subtitle: '起床时间写下来，作息会慢慢露出自己的样子。',
    emptyTip: '这个月还没有记录，第一次签到会从这里开始。'
  }
}

export default {
  components: {
    BrandIcon
  },
  data() {
    return {
      today: null,
      statistics: {},
      setting: {},
      calendar: [],
      publicBoard: [],
      currentYear: 0,
      currentMonth: 0,
      weekdayLabels: ['日', '一', '二', '三', '四', '五', '六'],
      content: createDefaultCheckinContent()
    }
  },
  computed: {
    statusText() {
      if (!this.today) {
        return '今天还没落笔，签到后会留下时间和状态。'
      }
      return `写于 ${this.formatDateTime(this.today.checkTime)} · ${this.today.status === 'ON_TIME' ? '准时' : '迟到'}`
    },
    monthTitle() {
      return `${this.currentYear}年${this.currentMonth}月`
    },
    currentUserId() {
      const authUser = getAuthUser()
      return authUser && authUser.id ? authUser.id : null
    },
    calendarMap() {
      return this.calendar.reduce((acc, item) => {
        acc[item.checkDate] = item
        return acc
      }, {})
    },
    calendarGrid() {
      if (!this.currentYear || !this.currentMonth) {
        return []
      }

      const firstDate = new Date(this.currentYear, this.currentMonth - 1, 1)
      const firstWeekday = firstDate.getDay()
      const daysInMonth = new Date(this.currentYear, this.currentMonth, 0).getDate()
      const prevMonthDays = new Date(this.currentYear, this.currentMonth - 1, 0).getDate()
      const today = this.formatDate(new Date())
      const result = []

      for (let index = 0; index < 42; index++) {
        let day = 0
        let currentMonth = true
        let dateObj = null

        if (index < firstWeekday) {
          day = prevMonthDays - firstWeekday + index + 1
          currentMonth = false
          dateObj = new Date(this.currentYear, this.currentMonth - 2, day)
        } else if (index >= firstWeekday + daysInMonth) {
          day = index - firstWeekday - daysInMonth + 1
          currentMonth = false
          dateObj = new Date(this.currentYear, this.currentMonth, day)
        } else {
          day = index - firstWeekday + 1
          dateObj = new Date(this.currentYear, this.currentMonth - 1, day)
        }

        const dateKey = this.formatDate(dateObj)
        const record = this.calendarMap[dateKey]
        result.push({
          key: `${dateKey}-${index}`,
          day,
          currentMonth,
          checked: !!record,
          status: record ? record.status : '',
          isToday: dateKey === today,
          dateKey
        })
      }

      return result
    }
  },
  onShow() {
    if (!this.ensureLoggedIn()) {
      return
    }
    const now = new Date()
    this.currentYear = now.getFullYear()
    this.currentMonth = now.getMonth() + 1
    this.loadData()
  },
  methods: {
    ensureLoggedIn() {
      if (getToken()) {
        return true
      }

      uni.navigateTo({ url: `/pages/login/index?redirect=${encodeURIComponent('/pages/checkin/index')}` })
      return false
    },
    async loadData() {
      const contentPromise = getHomeContent().then((data) => data.checkinHero || createDefaultCheckinContent()).catch(() => createDefaultCheckinContent())
      const [today, statistics, setting, calendar, publicBoard, content] = await Promise.all([
        getTodayCheckin(),
        getCheckinStatistics(),
        getSettings(),
        getCheckinCalendar(this.currentYear, this.currentMonth),
        getPublicCheckinBoard(),
        contentPromise
      ])
      this.today = today
      this.statistics = statistics
      this.setting = setting
      this.calendar = calendar
      this.publicBoard = publicBoard || []
      this.content = content
    },
    async handleCheckIn() {
      if (this.today) {
        return
      }
      this.today = await doCheckin()
      await this.loadData()
    },
    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return value.replace('T', ' ')
    },
    formatDate(date) {
      const year = date.getFullYear()
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    boardStatusText(item) {
      if (!item || !item.todayChecked) {
        return '未签到'
      }
      return item.todayStatus === 'ON_TIME' ? '已准时' : '已迟到'
    },
    boardSubmeta(item) {
      if (!item || !item.todayChecked || !item.todayCheckTime) {
        return '今天还没签到'
      }
      return `今天 ${this.formatDateTime(item.todayCheckTime)}`
    },
    boardAvatarLetter(item) {
      const name = item && (item.nickname || item.username) ? `${item.nickname || item.username}`.trim() : ''
      return name ? name.charAt(0).toUpperCase() : 'L'
    },
    async changeMonth(offset) {
      let year = this.currentYear
      let month = this.currentMonth + offset
      if (month < 1) {
        year -= 1
        month = 12
      } else if (month > 12) {
        year += 1
        month = 1
      }

      this.currentYear = year
      this.currentMonth = month
      await this.loadData()
    }
  }
}
</script>

<style scoped>
.checkin-hero {
  background: linear-gradient(135deg, #4d3a50, #7a5976 48%, #d49b69);
  color: #fffaf4;
}

.hero-topline,
.calendar-head,
.board-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.hero-badge,
.board-status,
.record-status {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 700;
}

.hero-badge.ok,
.board-status.ok,
.record-status.ok {
  color: #487436;
  background: rgba(232, 247, 228, 0.94);
}

.hero-badge.late,
.board-status.late,
.record-status.late {
  color: #95542e;
  background: rgba(255, 237, 214, 0.94);
}

.hero-badge.idle,
.board-status.idle {
  color: #7f748d;
  background: rgba(255, 248, 241, 0.18);
}

.hero-title {
  margin-top: 18rpx;
  font-size: 48rpx;
  line-height: 1.24;
  font-weight: 700;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.hero-subtitle {
  margin-top: 14rpx;
  font-size: 26rpx;
  line-height: 1.74;
  color: rgba(255, 247, 239, 0.9);
}

.hero-strip,
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
}

.hero-strip {
  margin-top: 24rpx;
}

.hero-strip-item {
  padding: 18rpx;
  border-radius: 22rpx;
  background: rgba(255, 248, 241, 0.14);
}

.hero-strip-label {
  font-size: 22rpx;
  color: rgba(255, 245, 237, 0.82);
}

.hero-strip-value {
  margin-top: 10rpx;
  font-size: 30rpx;
  font-weight: 700;
}

.status-card,
.stats-card,
.board-card,
.calendar-card,
.record-card {
  margin-top: 24rpx;
}

.status-inline {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin: 20rpx 0 24rpx;
}

.stats-title,
.board-title,
.calendar-title,
.record-title {
  margin-top: 16rpx;
}

.stats-grid {
  margin-top: 22rpx;
}

.stat-box {
  padding: 26rpx 20rpx;
  border-radius: 24rpx;
  text-align: center;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(249, 244, 255, 0.94));
}

.stat-value {
  color: #2f2944;
  font-size: 42rpx;
  font-weight: 700;
}

.stat-label,
.board-meta,
.record-time,
.month-text,
.weekday-cell {
  color: #857c92;
  font-size: 22rpx;
}

.board-row,
.record-row {
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 22rpx 0;
  border-bottom: 1rpx solid rgba(113, 103, 133, 0.12);
}

.board-row:last-child,
.record-row:last-child {
  border-bottom: 0;
}

.board-rank {
  width: 56rpx;
  height: 56rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(124, 155, 255, 0.12);
  color: #5f6fd7;
  font-weight: 700;
}

.board-avatar-wrap {
  width: 64rpx;
  height: 64rpx;
  border-radius: 20rpx;
  flex-shrink: 0;
  padding: 6rpx;
  background: rgba(255, 248, 241, 0.82);
  border: 1rpx solid rgba(124, 155, 255, 0.14);
}

.board-avatar-image,
.board-avatar-fallback {
  width: 100%;
  height: 100%;
  border-radius: 16rpx;
}

.board-avatar-image {
  display: block;
  background: #f5e7da;
}

.board-avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24rpx;
  font-weight: 800;
  background: linear-gradient(135deg, #7c6fd7, #f28c45);
}

.board-main {
  flex: 1;
  min-width: 0;
}

.board-name,
.record-date {
  color: #332d44;
  font-size: 28rpx;
  font-weight: 700;
}

.self-badge {
  padding: 6rpx 12rpx;
  border-radius: 999rpx;
  background: rgba(242, 140, 69, 0.16);
  color: #b35c32;
  font-size: 20rpx;
  font-weight: 700;
}

.muted {
  margin-top: 4rpx;
}

.month-switch {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.month-btn {
  width: 50rpx;
  height: 50rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(124, 155, 255, 0.12);
  color: #566cd9;
  font-weight: 700;
}

.weekday-row,
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10rpx;
}

.weekday-row {
  margin: 20rpx 0 12rpx;
}

.calendar-cell {
  min-height: 86rpx;
  padding: 14rpx 8rpx;
  border-radius: 18rpx;
  background: rgba(255, 255, 255, 0.7);
  text-align: center;
}

.mutedCell {
  opacity: 0.42;
}

.todayCell {
  box-shadow: inset 0 0 0 2rpx rgba(242, 140, 69, 0.38);
}

.checked {
  background: rgba(231, 246, 228, 0.8);
}

.lateCell {
  background: rgba(255, 237, 214, 0.82);
}

.calendar-day {
  color: #40374f;
  font-size: 24rpx;
  font-weight: 600;
}

.calendar-mark {
  margin-top: 10rpx;
  color: #7f695a;
  font-size: 20rpx;
}

@media screen and (max-width: 380px) {
  .hero-strip,
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .calendar-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
