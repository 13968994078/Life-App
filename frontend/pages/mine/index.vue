<template>
  <view class="page mine-page">
    <view class="card mine-hero profile-hero">
      <view class="hero-topline">
        <view class="section-label">
          <brand-icon name="profile" :size="24" />
          <text>个人主页</text>
        </view>
        <view class="hero-note">{{ todayGreeting }}</view>
      </view>
      <view class="hero-title">{{ content.title }}</view>
      <view class="hero-subtitle">{{ content.subtitle }}</view>
      <view class="hero-strip">
        <view class="hero-strip-item">
          <view class="hero-strip-label">上次饭点</view>
          <view class="hero-strip-value">{{ latestFoodName }}</view>
        </view>
        <view class="hero-strip-item">
          <view class="hero-strip-label">菜单</view>
          <view class="hero-strip-value">{{ foods.length }} 项</view>
        </view>
        <view class="hero-strip-item">
          <view class="hero-strip-label">签到席位</view>
          <view class="hero-strip-value">{{ rankText }}</view>
        </view>
      </view>
    </view>

    <view class="card overview-card">
      <view class="section-label">
        <brand-icon name="stats" :size="24" />
        <text>本页索引</text>
      </view>
      <view class="section-title overview-title">最近的线索</view>
      <view class="stats-grid">
        <view v-for="item in lifeStats" :key="item.label" class="stat-card">
          <view class="stat-value">{{ item.value }}</view>
          <view class="stat-label">{{ item.label }}</view>
          <view class="stat-hint">{{ item.hint }}</view>
        </view>
      </view>
    </view>

    <view class="card status-card">
      <view class="status-main">
        <view>
          <view class="section-label">
            <brand-icon name="petal" :size="24" />
            <text>今日小结</text>
          </view>
          <view class="section-title status-title">{{ todayStatusTitle }}</view>
          <view class="section-subtext">{{ todayStatusDesc }}</view>
        </view>
        <view class="status-badge" :class="todayStatusClass">
          <view class="status-badge-value">{{ todayStatusText }}</view>
          <view class="status-badge-label">{{ todayTimeText }}</view>
        </view>
      </view>
    </view>

    <view class="card activity-card">
      <view class="section-label">
        <brand-icon name="history" :size="24" />
        <text>页边记录</text>
      </view>
      <view class="section-title activity-title">最近记录</view>
      <view class="activity-list">
        <view v-for="item in recentActivities" :key="item.title" class="activity-row">
          <view class="activity-dot"></view>
          <view class="activity-main">
            <view class="activity-name">{{ item.title }}</view>
            <view class="activity-meta">{{ item.meta }}</view>
          </view>
        </view>
      </view>
    </view>

    <view class="card management-card">
      <view class="section-label">
        <brand-icon name="list" :size="24" />
        <text>目录</text>
      </view>
      <view class="section-title management-title">常用入口，放在手边</view>
      <view class="management-list">
        <view v-for="item in managementItems" :key="item.title" class="management-row" @tap="handleManagement(item.action)">
          <view class="management-icon">
            <brand-icon :name="item.icon" :size="34" />
          </view>
          <view class="management-main">
            <view class="management-name">{{ item.title }}</view>
            <view class="management-desc">{{ item.desc }}</view>
          </view>
          <view class="management-arrow">›</view>
        </view>
      </view>
    </view>

    <view class="card setting-card">
      <view class="section-label">
        <brand-icon name="target" :size="24" />
        <text>清晨设置</text>
      </view>
      <view class="section-title setting-title">给清晨定一个轻一点的闹钟</view>
      <picker mode="time" :value="form.wakeTargetTime" @change="onTimeChange">
        <view class="setting-row pick-row">
          <view class="setting-inline-icon">
            <brand-icon name="target" :size="28" />
          </view>
          <view>
            <view class="setting-name">目标时间</view>
            <view class="setting-desc">先定一个不太为难自己的时间。</view>
          </view>
          <view class="setting-value">{{ form.wakeTargetTime || '--:--' }}</view>
        </view>
      </picker>
      <view class="setting-row toggle-row">
        <view class="setting-inline-icon">
          <brand-icon name="notification" :size="28" />
        </view>
        <view>
          <view class="setting-name">晨间提醒</view>
          <view class="setting-desc">{{ form.notificationEnabled ? '提醒已开启。' : '提醒未开启。' }}</view>
        </view>
        <switch :checked="form.notificationEnabled" color="#ff8b2b" @change="onNotificationChange" />
      </view>
      <view class="primary-btn" @tap="handleSave">保存设置</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { getCurrentUser } from '../../api/auth'
import { getCheckinStatistics, getPublicCheckinBoard, getTodayCheckin } from '../../api/checkin'
import { getHomeContent } from '../../api/content'
import { getFoodHistory, getFoodList } from '../../api/food'
import { getSettings, updateSettings } from '../../api/settings'
import { getAuthUser, getToken, logout, setAuthUser } from '../../utils/auth'

function createDefaultMineContent() {
  return {
    title: '你的生活小账本',
    subtitle: '饭点、签到、提醒，都收在这一页，翻起来不费劲。'
  }
}

export default {
  components: {
    BrandIcon
  },
  data() {
    return {
      authUser: null,
      setting: {},
      todayCheckin: null,
      statistics: {},
      publicBoard: [],
      foods: [],
      history: [],
      content: createDefaultMineContent(),
      form: {
        wakeTargetTime: '07:00',
        notificationEnabled: false
      }
    }
  },
  computed: {
    todayGreeting() {
      const hour = new Date().getHours()
      if (hour < 11) {
        return '早上好'
      }
      if (hour < 18) {
        return '今天慢慢翻页'
      }
      return '今晚把灯调暗一点'
    },
    currentUserId() {
      if (!this.authUser || this.authUser.id === null || this.authUser.id === undefined) {
        return null
      }
      return String(this.authUser.id)
    },
    rankText() {
      if (!this.currentUserId || !this.publicBoard.length) {
        return '--'
      }
      const index = this.publicBoard.findIndex((item) => String(item.userId) === this.currentUserId)
      return index >= 0 ? `第 ${index + 1} 名` : '--'
    },
    latestFoodName() {
      return this.history.length && this.history[0].foodName ? this.history[0].foodName : '还没抽'
    },
    lifeStats() {
      return [
        {
          label: '连续签到',
          value: `${this.statistics.streakDays || 0} 天`,
          hint: '坚持下去'
        },
        {
          label: '累计签到',
          value: `${this.statistics.totalDays || 0} 天`,
          hint: '成功的痕迹'
        },
        {
          label: '美食清单',
          value: `${this.foods.length} 项`,
          hint: '转盘里的名字'
        },
        {
          label: '抽取记录',
          value: `${this.history.length} 条`,
          hint: '饭点留下的回声'
        }
      ]
    },
    todayStatusTitle() {
      return this.todayCheckin ? '今天已签到' : '今天还没签到'
    },
    todayStatusDesc() {
      if (!this.todayCheckin) {
        return `目标时间 ${this.form.wakeTargetTime || '--:--'}，签到后会写进今天的记录。`
      }
      return this.todayCheckin.status === 'ON_TIME'
        ? '今天准时签到。'
        : '今天成功签到，只是比目标晚一点。'
    },
    todayStatusText() {
      if (!this.todayCheckin) {
        return '待签到'
      }
      return this.todayCheckin.status === 'ON_TIME' ? '准时' : '已签到'
    },
    todayStatusClass() {
      if (!this.todayCheckin) {
        return 'idle'
      }
      return this.todayCheckin.status === 'ON_TIME' ? 'ok' : 'late'
    },
    todayTimeText() {
      return this.todayCheckin && this.todayCheckin.checkTime ? this.formatDateTime(this.todayCheckin.checkTime) : this.form.wakeTargetTime || '--:--'
    },
    recentActivities() {
      const activities = []

      if (this.history.length) {
        const latest = this.history[0]
        activities.push({
          title: `上次饭点写下「${latest.foodName || '未命名美食'}」`,
          meta: this.formatDateTime(latest.createdAt)
        })
      } else {
        activities.push({
          title: '饭点还没留下记录',
          meta: '去美食页转一次，这里就会出现。'
        })
      }

      activities.push({
        title: this.todayCheckin ? '今天已签到' : '今天还没签到',
        meta: this.todayCheckin ? this.formatDateTime(this.todayCheckin.checkTime) : `目标时间 ${this.form.wakeTargetTime || '--:--'}`
      })

      activities.push({
        title: this.form.notificationEnabled ? '晨间提醒已开启' : '晨间提醒未开启',
        meta: this.form.notificationEnabled ? '到点会轻轻提醒你。' : '容易忘记时，可以打开提醒。'
      })

      return activities
    },
    managementItems() {
      return [
        {
          icon: 'profile',
          title: '个人资料',
          desc: '把昵称和头像整理一下。',
          action: 'profile'
        },
        {
          icon: 'food',
          title: '我的美食库',
          desc: '把常吃和想试的都收进来。',
          action: 'food'
        },
        {
          icon: 'history',
          title: '抽取记录',
          desc: '回看饭点留下的记录。',
          action: 'history'
        },
        {
          icon: 'key',
          title: '修改密码',
          desc: '给账号换一把新钥匙。',
          action: 'password'
        },
        {
          icon: 'mine',
          title: '切换账号',
          desc: '换一个账号登录。',
          action: 'switchAccount'
        },
        {
          icon: 'logout',
          title: '退出登录',
          desc: '清除本机登录状态。',
          action: 'logout'
        }
      ]
    }
  },
  onShow() {
    if (!this.ensureLoggedIn()) {
      return
    }
    this.authUser = getAuthUser()
    this.loadPageData()
  },
  methods: {
    ensureLoggedIn() {
      if (getToken()) {
        return true
      }

      uni.navigateTo({ url: `/pages/login/index?redirect=${encodeURIComponent('/pages/mine/index')}` })
      return false
    },
    async loadPageData() {
      try {
        const [user, setting, todayCheckin, statistics, publicBoard, foods, history, contentData] = await Promise.all([
          getCurrentUser(),
          getSettings(),
          getTodayCheckin(),
          getCheckinStatistics(),
          getPublicCheckinBoard(),
          getFoodList(),
          getFoodHistory(),
          getHomeContent().catch(() => ({ mineHero: createDefaultMineContent() }))
        ])

        this.authUser = user
        setAuthUser(user)
        this.setting = setting || {}
        this.form = {
          wakeTargetTime: this.setting.wakeTargetTime || '07:00',
          notificationEnabled: !!this.setting.notificationEnabled
        }
        this.todayCheckin = todayCheckin || null
        this.statistics = statistics || {}
        this.publicBoard = Array.isArray(publicBoard) ? publicBoard : []
        this.foods = Array.isArray(foods) ? foods : []
        this.history = Array.isArray(history) ? history : []
        this.content = contentData.mineHero || createDefaultMineContent()
      } catch (error) {
        uni.showToast({ title: error.message || '页面加载失败', icon: 'none' })
      }
    },
    onTimeChange(event) {
      this.form.wakeTargetTime = event.detail.value
    },
    onNotificationChange(event) {
      this.form.notificationEnabled = event.detail.value
    },
    async handleSave() {
      try {
        this.setting = await updateSettings(this.form)
        this.form = {
          wakeTargetTime: this.setting.wakeTargetTime,
          notificationEnabled: !!this.setting.notificationEnabled
        }
        uni.showToast({ title: '设置已保存', icon: 'success' })
      } catch (error) {
        uni.showToast({ title: error.message || '设置没保存成功', icon: 'none' })
      }
    },
    handleManagement(action) {
      if (action === 'profile') {
        uni.navigateTo({ url: '/pages/mine/profile' })
        return
      }
      if (action === 'food') {
        uni.switchTab({ url: '/pages/food/index' })
        return
      }
      if (action === 'history') {
        uni.navigateTo({ url: '/pages/food/history' })
        return
      }
      if (action === 'password') {
        uni.navigateTo({ url: '/pages/mine/password' })
        return
      }
      if (action === 'switchAccount') {
        uni.navigateTo({ url: '/pages/login/index?force=true' })
        return
      }
      if (action === 'logout') {
        this.handleLogout()
      }
    },
    handleLogout() {
      logout()
      this.authUser = null
      this.todayCheckin = null
      this.statistics = {}
      this.publicBoard = []
      this.foods = []
      this.history = []
      uni.showToast({ title: '已退出', icon: 'success' })
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/login/index?force=true' })
      }, 500)
    },
    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return value.replace('T', ' ')
    }
  }
}
</script>

<style scoped>
.mine-hero {
  background: linear-gradient(135deg, #4d3631, #8d573e 58%, #ddb078);
  color: #fffaf4;
}

.hero-topline,
.status-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.hero-note {
  font-size: 22rpx;
  color: rgba(255, 246, 236, 0.82);
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
  gap: 16rpx;
}

.hero-strip {
  grid-template-columns: repeat(3, 1fr);
  margin-top: 24rpx;
}

.hero-strip-item {
  padding: 18rpx 16rpx;
  border-radius: 22rpx;
  background: rgba(255, 248, 241, 0.14);
}

.hero-strip-label {
  font-size: 20rpx;
  color: rgba(255, 245, 237, 0.8);
}

.hero-strip-value {
  margin-top: 10rpx;
  font-size: 28rpx;
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.overview-card,
.status-card,
.activity-card,
.management-card,
.setting-card {
  margin-top: 24rpx;
}

.overview-title,
.activity-title,
.management-title,
.setting-title,
.status-title {
  margin-top: 16rpx;
}

.stats-grid {
  grid-template-columns: repeat(2, 1fr);
  margin-top: 22rpx;
}

.stat-card {
  padding: 24rpx 20rpx;
  border-radius: 24rpx;
  background: linear-gradient(180deg, rgba(255, 248, 240, 0.98), rgba(255, 252, 248, 0.98));
}

.stat-value {
  color: #2f221d;
  font-size: 38rpx;
  font-weight: 700;
}

.stat-label {
  margin-top: 10rpx;
  color: #7b533c;
  font-size: 24rpx;
  font-weight: 700;
}

.stat-hint,
.activity-meta,
.management-desc,
.setting-desc {
  margin-top: 8rpx;
  color: #8a7b70;
  font-size: 22rpx;
  line-height: 1.55;
}

.status-main {
  align-items: stretch;
}

.status-badge {
  width: 200rpx;
  flex-shrink: 0;
  border-radius: 28rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24rpx 18rpx;
}

.status-badge.ok {
  background: rgba(231, 246, 228, 0.96);
  color: #477234;
}

.status-badge.late {
  background: rgba(255, 237, 210, 0.96);
  color: #8a5532;
}

.status-badge.idle {
  background: rgba(255, 248, 240, 0.96);
  color: #8a6249;
}

.status-badge-value {
  font-size: 32rpx;
  font-weight: 800;
}

.status-badge-label {
  margin-top: 10rpx;
  font-size: 20rpx;
  line-height: 1.5;
  text-align: center;
}

.activity-row,
.management-row,
.setting-row {
  display: flex;
  align-items: center;
  gap: 18rpx;
}

.activity-list,
.management-list {
  margin-top: 18rpx;
}

.activity-row {
  padding: 18rpx 0;
  border-bottom: 1rpx solid rgba(118, 86, 66, 0.1);
}

.activity-row:last-child {
  border-bottom: 0;
}

.activity-dot {
  width: 18rpx;
  height: 18rpx;
  border-radius: 50%;
  background: #d8a55a;
  box-shadow: 0 0 0 8rpx rgba(216, 165, 90, 0.12);
}

.activity-main,
.management-main {
  flex: 1;
  min-width: 0;
}

.activity-name,
.management-name,
.setting-name {
  color: #30231e;
  font-size: 28rpx;
  font-weight: 700;
}

.management-row {
  padding: 20rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.68);
  border: 1rpx solid rgba(118, 86, 66, 0.08);
  margin-top: 12rpx;
}

.management-icon {
  width: 68rpx;
  height: 68rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(216, 165, 90, 0.12);
}

.management-arrow {
  color: #b1896b;
  font-size: 42rpx;
}

.pick-row,
.toggle-row {
  justify-content: space-between;
  padding: 22rpx 24rpx;
  border-radius: 22rpx;
  background: linear-gradient(180deg, #fffaf7, #fff3eb);
  border: 1rpx solid rgba(233, 222, 211, 0.9);
  margin-top: 20rpx;
}

.setting-value {
  flex-shrink: 0;
  color: #8f5535;
  font-size: 34rpx;
  font-weight: 700;
}

.setting-inline-icon {
  width: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.setting-card .primary-btn {
  margin-top: 24rpx;
}

@media screen and (max-width: 380px) {
  .hero-strip,
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .status-main {
    flex-direction: column;
  }

  .status-badge {
    width: auto;
  }
}
</style>
