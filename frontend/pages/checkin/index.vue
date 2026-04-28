<template>
  <view class="page">
    <view class="card dawn-banner stage-card">
      <view class="banner-row">
        <view class="banner-mark">AM</view>
        <view class="banner-note">Morning Ledger</view>
      </view>
      <view class="banner-title">起床签到</view>
      <view class="banner-subtitle">按下今天的开始键，把连续早起一点点攒出来。签到不只是记录时间，更是看见你的节奏有没有开始稳定下来。</view>
      <view class="banner-strip">
        <view class="banner-metric">
          <view class="banner-metric-label">目标起床</view>
          <view class="banner-metric-value">{{ setting.wakeTargetTime || '--:--' }}</view>
        </view>
        <view class="banner-metric">
          <view class="banner-metric-label">连续签到</view>
          <view class="banner-metric-value">{{ statistics.streakDays || 0 }} 天</view>
        </view>
      </view>
    </view>

    <view class="card status-card">
      <view class="editorial-kicker card-kicker">
        <brand-icon name="checkin" :size="26" />
        Today
      </view>
      <view class="section-title section-no-margin">今日签到</view>
      <view class="status-text">{{ statusText }}</view>
      <view class="status-subtext">目标起床时间：{{ setting.wakeTargetTime || '--:--' }}</view>
      <view class="primary-btn" @tap="handleCheckIn">{{ today ? '今日已签到' : '立即签到' }}</view>
    </view>

    <view class="card stats-card">
      <view class="editorial-kicker card-kicker">
        <brand-icon name="stats" :size="26" />
        Habit Score
      </view>
      <view class="section-title section-no-margin">签到统计</view>
      <view class="section-subtext stats-tip">越稳定，连续天数越漂亮。</view>
      <view class="stat-row">
        <view class="stat-box">
          <brand-icon name="stats" class="stat-icon" :size="62" />
          <view class="stat-value">{{ statistics.streakDays || 0 }}</view>
          <view class="stat-label">连续天数</view>
        </view>
        <view class="stat-box">
          <brand-icon name="calendar" class="stat-icon" :size="62" />
          <view class="stat-value">{{ statistics.totalDays || 0 }}</view>
          <view class="stat-label">累计天数</view>
        </view>
      </view>
    </view>

    <view class="card history-card public-board-card">
      <view class="editorial-kicker card-kicker">
        <brand-icon name="board" :size="26" />
        Public Board
      </view>
      <view class="section-title section-no-margin">公开签到榜</view>
      <view class="section-subtext stats-tip">这里能看到其他用户今天有没有签到，以及谁最近更稳定。</view>
      <view v-for="(item, index) in publicBoard" :key="item.userId" class="board-row">
        <view class="board-rank">{{ index + 1 }}</view>
        <view class="board-main">
          <view class="board-title-line">
            <view class="board-name">{{ item.nickname || item.username }}</view>
            <view v-if="item.userId === currentUserId" class="self-badge">我</view>
          </view>
          <view class="board-meta">@{{ item.username }} · 连续 {{ item.streakDays || 0 }} 天 · 累计 {{ item.totalDays || 0 }} 天</view>
          <view class="board-submeta">{{ boardSubmeta(item) }}</view>
        </view>
        <view class="board-status" :class="item.todayStatus === 'ON_TIME' ? 'ok' : item.todayStatus === 'LATE' ? 'late' : 'idle'">
          {{ boardStatusText(item) }}
        </view>
      </view>
      <view v-if="!publicBoard.length" class="empty-text">还没有可展示的公开签到数据</view>
    </view>

    <view class="card history-card calendar-card">
      <view class="calendar-head">
        <view>
          <view class="editorial-kicker card-kicker">
            <brand-icon name="calendar" :size="26" />
            Monthly View
          </view>
          <view class="section-title section-no-margin">签到月历</view>
          <view class="section-subtext stats-tip">一眼看清本月哪几天准时，哪几天迟到了。</view>
        </view>
        <view class="month-switch">
          <view class="month-btn" @tap="changeMonth(-1)"><</view>
          <view class="month-text">{{ monthTitle }}</view>
          <view class="month-btn" @tap="changeMonth(1)">></view>
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
          :class="{
            muted: !item.currentMonth,
            today: item.isToday,
            checked: item.checked,
            lateCell: item.status === 'LATE'
          }"
        >
          <view class="calendar-day">{{ item.day }}</view>
          <view v-if="item.checked" class="calendar-mark">{{ item.status === 'ON_TIME' ? '准' : '迟' }}</view>
        </view>
      </view>
    </view>

    <view class="card history-card record-card">
      <view class="editorial-kicker card-kicker">
        <brand-icon name="history" :size="26" />
        Record List
      </view>
      <view class="section-title section-no-margin">本月记录</view>
      <view class="section-subtext stats-tip">准时和迟到都记录下来，方便你看趋势。</view>
      <view v-for="item in calendar" :key="item.id" class="history-row">
        <view>
          <view class="history-date">{{ item.checkDate }}</view>
          <view class="history-time">{{ formatDateTime(item.checkTime) }}</view>
        </view>
        <view class="history-status" :class="item.status === 'ON_TIME' ? 'ok' : 'late'">
          {{ item.status === 'ON_TIME' ? '准时' : '迟到' }}
        </view>
      </view>
      <view v-if="!calendar.length" class="empty-text">本月还没有签到记录</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { doCheckin, getCheckinCalendar, getCheckinStatistics, getPublicCheckinBoard, getTodayCheckin } from '../../api/checkin'
import { getSettings } from '../../api/settings'
import { getAuthUser, getToken } from '../../utils/auth'

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
      weekdayLabels: ['日', '一', '二', '三', '四', '五', '六']
    }
  },
  computed: {
    statusText() {
      if (!this.today) {
        return '今天还没有签到'
      }
      return `已签到：${this.formatDateTime(this.today.checkTime)} · ${this.today.status === 'ON_TIME' ? '准时' : '迟到'}`
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
      const [today, statistics, setting, calendar, publicBoard] = await Promise.all([
        getTodayCheckin(),
        getCheckinStatistics(),
        getSettings(),
        getCheckinCalendar(this.currentYear, this.currentMonth),
        getPublicCheckinBoard()
      ])
      this.today = today
      this.statistics = statistics
      this.setting = setting
      this.calendar = calendar
      this.publicBoard = publicBoard || []
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
        return '今天暂未签到'
      }
      return `今天 ${this.formatDateTime(item.todayCheckTime)}`
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
.dawn-banner {
  margin-bottom: 24rpx;
  background:
    radial-gradient(circle at top right, rgba(255, 245, 233, 0.28), transparent 24%),
    linear-gradient(135deg, #5b445c, #a85b3f 54%, #efc07d);
  color: #fffdfb;
}

.banner-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.banner-mark {
  display: inline-flex;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.16);
  font-size: 22rpx;
  margin-bottom: 18rpx;
}

.banner-note {
  font-size: 22rpx;
  line-height: 1.6;
  color: rgba(255, 248, 241, 0.82);
}

.banner-title {
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

.banner-strip {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
  margin-top: 26rpx;
}

.banner-metric {
  padding: 18rpx 18rpx 20rpx;
  border-radius: 22rpx;
  background: rgba(255, 249, 241, 0.14);
  border: 1rpx solid rgba(255, 248, 240, 0.16);
}

.banner-metric-label {
  font-size: 20rpx;
  letter-spacing: 2rpx;
  color: rgba(255, 246, 237, 0.76);
}

.banner-metric-value {
  margin-top: 10rpx;
  font-size: 32rpx;
  font-weight: 700;
  color: #fff8f1;
}

.status-card {
  margin-bottom: 24rpx;
  background: linear-gradient(180deg, rgba(255, 252, 247, 0.98), rgba(248, 241, 255, 0.94));
}

.section-no-margin {
  margin-bottom: 0;
}

.card-kicker {
  margin-bottom: 14rpx;
}

.status-text {
  margin: 16rpx 0 12rpx;
  color: #4f4965;
  font-size: 30rpx;
  font-weight: 600;
}

.status-subtext {
  margin-bottom: 24rpx;
  color: #8a82a0;
}

.stats-card {
  background: linear-gradient(180deg, rgba(255, 251, 247, 0.98), rgba(246, 241, 255, 0.96));
}

.stats-tip {
  margin-top: 10rpx;
  margin-bottom: 20rpx;
}

.stat-row {
  display: flex;
  gap: 24rpx;
}

.stat-box {
  flex: 1;
  background: rgba(255, 255, 255, 0.92);
  border-radius: 24rpx;
  padding: 28rpx;
  text-align: center;
  box-shadow: inset 0 0 0 1rpx rgba(124, 155, 255, 0.1);
}

.stat-icon {
  margin: 0 auto 16rpx;
}

.stat-value {
  font-size: 44rpx;
  font-weight: 700;
  color: #1d3557;
}

.stat-label {
  margin-top: 8rpx;
  color: #7e8799;
}

.history-card {
  margin-top: 24rpx;
}

.public-board-card {
  background: linear-gradient(180deg, rgba(255, 252, 248, 0.98), rgba(245, 240, 255, 0.96));
}

.calendar-card,
.record-card {
  background: linear-gradient(180deg, rgba(255, 252, 248, 0.98), rgba(247, 244, 255, 0.96));
}

.board-row {
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid rgba(93, 109, 170, 0.12);
}

.board-row:last-of-type {
  border-bottom: 0;
}

.board-rank {
  width: 54rpx;
  height: 54rpx;
  flex-shrink: 0;
  border-radius: 18rpx;
  background: rgba(124, 155, 255, 0.12);
  color: #5f6fd7;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 700;
}

.board-main {
  flex: 1;
  min-width: 0;
}

.board-title-line {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.board-name {
  color: #3f3a55;
  font-size: 30rpx;
  font-weight: 700;
}

.self-badge {
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(242, 140, 69, 0.16);
  color: #b35c32;
  font-size: 20rpx;
  font-weight: 700;
}

.board-meta,
.board-submeta {
  color: #8d87a0;
  font-size: 22rpx;
  line-height: 1.6;
}

.board-meta {
  margin-top: 8rpx;
}

.board-submeta {
  margin-top: 4rpx;
}

.board-status {
  min-width: 112rpx;
  text-align: center;
  border-radius: 999rpx;
  padding: 10rpx 18rpx;
  font-size: 24rpx;
}

.idle {
  color: #8f8aa2;
  background: rgba(141, 135, 160, 0.12);
}

.calendar-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.month-switch {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding-top: 6rpx;
}

.month-btn {
  width: 52rpx;
  height: 52rpx;
  border-radius: 16rpx;
  background: rgba(124, 155, 255, 0.12);
  color: #566cd9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.month-text {
  min-width: 120rpx;
  text-align: center;
  font-size: 24rpx;
  color: #5a6078;
  font-weight: 600;
}

.weekday-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10rpx;
  margin: 18rpx 0 10rpx;
}

.weekday-cell {
  text-align: center;
  font-size: 22rpx;
  color: #8d87a0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10rpx;
}

.calendar-cell {
  min-height: 108rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.94);
  border: 1rpx solid rgba(124, 155, 255, 0.08);
  padding: 14rpx 10rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.calendar-day {
  font-size: 24rpx;
  color: #4d4c63;
  font-weight: 600;
}

.calendar-mark {
  align-self: flex-start;
  padding: 6rpx 12rpx;
  border-radius: 999rpx;
  background: #eaf7ef;
  color: #2e8b57;
  font-size: 20rpx;
  font-weight: 700;
}

.muted {
  opacity: 0.45;
}

.today {
  box-shadow: inset 0 0 0 2rpx rgba(92, 124, 250, 0.18);
}

.checked {
  background: linear-gradient(180deg, #f9fbff, #eef4ff);
}

.lateCell .calendar-mark {
  background: #fff4e5;
  color: #c57a1c;
}

.history-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid rgba(93, 109, 170, 0.12);
}

.history-row:last-child {
  border-bottom: 0;
}

.history-date {
  font-size: 30rpx;
  font-weight: 600;
  color: #3f3a55;
}

.history-time {
  margin-top: 6rpx;
  color: #8d87a0;
  font-size: 24rpx;
}

.history-status {
  min-width: 96rpx;
  text-align: center;
  border-radius: 999rpx;
  padding: 8rpx 18rpx;
  font-size: 24rpx;
}

.ok {
  color: #2e8b57;
  background: #eaf7ef;
}

.late {
  color: #c57a1c;
  background: #fff4e5;
}

.empty-text {
  color: #9aa3b2;
  text-align: center;
  padding: 24rpx 0 8rpx;
}
</style>
