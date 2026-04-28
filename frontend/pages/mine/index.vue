<template>
    <view class="page">
      <view class="card mine-banner account-stage">
        <view class="stage-topline">
          <view class="editorial-kicker stage-kicker">
            <brand-icon name="mine" :size="26" />
            Personal Ledger
          </view>
          <view class="stage-note">账号与作息</view>
        </view>
      <view class="banner-title">把个人节奏收进一张清晰的版面。</view>
      <view class="banner-subtitle">这里放账号状态和早起目标，方便你把习惯参数固定下来，不用每次都重新找。</view>
      <view class="stage-strip">
        <view class="stage-metric">
          <view class="stage-metric-label">目标起床</view>
          <view class="stage-metric-value">{{ form.wakeTargetTime || '--:--' }}</view>
        </view>
        <view class="stage-metric">
          <view class="stage-metric-label">提醒状态</view>
          <view class="stage-metric-value">{{ form.notificationEnabled ? '已开启' : '未开启' }}</view>
        </view>
      </view>
    </view>

    <view class="card profile-card identity-card">
      <view class="profile-aside">
        <view class="avatar-wrap">
          <view class="avatar">{{ avatarLetter }}</view>
        </view>
        <view class="profile-overline">Member Profile</view>
      </view>
      <view class="profile-main">
        <view class="name">{{ userName }}</view>
        <view class="desc">{{ userDesc }}</view>
        <view class="profile-tags">
          <view class="soft-chip">{{ isLoggedIn ? '已登录' : '未登录' }}</view>
          <view class="soft-chip">多用户模式</view>
        </view>
        <view class="profile-meta-grid">
          <view class="meta-item">
            <view class="meta-label">登录账号</view>
            <view class="meta-value account-value">@{{ userAccount }}</view>
          </view>
          <view class="meta-item">
            <view class="meta-label">数据范围</view>
            <view class="meta-value">美食 / 签到 / 设置</view>
          </view>
          <view class="meta-item">
            <view class="meta-label">当前入口</view>
            <view class="meta-value">{{ isLoggedIn ? '多用户数据已隔离' : '等待登录' }}</view>
          </view>
        </view>
      </view>
    </view>

    <view class="card auth-card settings-sheet">
      <view class="sheet-head">
        <view>
          <view class="editorial-kicker sheet-kicker">
            <brand-icon name="mine" :size="26" />
            Account Access
          </view>
          <view class="section-title section-no-margin">账号入口</view>
        </view>
        <view class="sheet-badge">{{ isLoggedIn ? 'Active' : 'Guest' }}</view>
      </view>
      <view class="setting-desc">登录后即可同步查看个人资料、起床设置和历史数据。</view>
      <view class="auth-actions">
        <navigator class="primary-btn auth-btn" :url="loginEntryUrl">{{ isLoggedIn ? '切换账号' : '前往登录' }}</navigator>
        <view v-if="isLoggedIn" class="secondary-btn auth-btn" @tap="handleLogout">退出登录</view>
      </view>
    </view>

    <view class="card password-card settings-sheet password-sheet">
      <view class="editorial-kicker sheet-kicker">
        <brand-icon name="key" :size="26" />
        Password Security
      </view>
      <view class="section-title section-no-margin">修改密码</view>
      <view class="setting-desc">输入旧密码和新密码，修改后下次登录请使用新密码。</view>
      <view class="password-form">
        <view class="form-field">
          <view class="form-label">旧密码</view>
          <input v-model="passwordForm.oldPassword" class="password-input-field" password placeholder="请输入旧密码" />
        </view>
        <view class="form-field">
          <view class="form-label">新密码</view>
          <input v-model="passwordForm.newPassword" class="password-input-field" password placeholder="请输入新密码" />
        </view>
        <view class="form-field">
          <view class="form-label">确认新密码</view>
          <input v-model="passwordForm.confirmPassword" class="password-input-field" password placeholder="请再次输入新密码" />
        </view>
      </view>
      <view class="primary-btn" :class="{ disabled: passwordSubmitting }" @tap="handleChangePassword">{{ passwordSubmitting ? '提交中...' : '确认修改' }}</view>
    </view>

    <view class="card settings-card settings-sheet">
      <view class="editorial-kicker sheet-kicker">
        <brand-icon name="wake" :size="26" />
        Wake Settings
      </view>
      <view class="section-title section-no-margin">起床设置</view>
      <view class="setting-desc">目标时间越稳定，签到统计越有参考意义。</view>
      <picker mode="time" :value="form.wakeTargetTime" @change="onTimeChange">
        <view class="setting-picker">
          <view>
            <view class="picker-label">目标时间</view>
            <view class="picker-hint">建议固定一个容易坚持的起床点。</view>
          </view>
          <view class="picker-value">{{ form.wakeTargetTime || '--:--' }}</view>
        </view>
      </picker>
      <view class="toggle-row">
        <view>
          <view class="setting-row">提醒开关</view>
          <view class="toggle-hint">{{ form.notificationEnabled ? '当前已开启晨间提醒。' : '当前未开启晨间提醒。' }}</view>
        </view>
        <switch :checked="form.notificationEnabled" color="#ff8b2b" @change="onNotificationChange" />
      </view>
      <view class="primary-btn" @tap="handleSave">保存设置</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { changePassword, getCurrentUser } from '../../api/auth'
import { getSettings, updateSettings } from '../../api/settings'
import { getAuthUser, getToken, logout, setAuthUser } from '../../utils/auth'

export default {
  components: {
    BrandIcon
  },
  data() {
    return {
      authUser: null,
      setting: {},
      form: {
        wakeTargetTime: '07:00',
        notificationEnabled: false
      },
      passwordSubmitting: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    }
  },
  computed: {
    isLoggedIn() {
      return !!getToken()
    },
    loginEntryUrl() {
      return this.isLoggedIn ? '/pages/login/index?force=true' : '/pages/login/index'
    },
    userName() {
      return this.authUser && this.authUser.nickname ? this.authUser.nickname : '奇幻妙妙屋用户'
    },
    userAccount() {
      return this.authUser && this.authUser.username ? this.authUser.username : '--'
    },
    avatarLetter() {
      const name = this.userName.trim()
      return name ? name.charAt(0).toUpperCase() : 'L'
    },
    userDesc() {
      return this.isLoggedIn ? '当前账号已连接你的个人美食、签到和设置数据，每位用户的数据彼此隔离。' : '登录后可访问完整个人数据。'
    }
  },
  onShow() {
    if (!this.ensureLoggedIn()) {
      return
    }
    this.authUser = getAuthUser()
    this.loadUser()
    this.loadSetting()
  },
  methods: {
    ensureLoggedIn() {
      if (getToken()) {
        return true
      }

      uni.navigateTo({ url: `/pages/login/index?redirect=${encodeURIComponent('/pages/mine/index')}` })
      return false
    },
    async loadUser() {
      if (!this.isLoggedIn) {
        return
      }
      try {
        const user = await getCurrentUser()
        this.authUser = user
        setAuthUser(user)
      } catch (error) {
        this.authUser = getAuthUser()
      }
    },
    async loadSetting() {
      this.setting = await getSettings()
      this.form = {
        wakeTargetTime: this.setting.wakeTargetTime || '07:00',
        notificationEnabled: !!this.setting.notificationEnabled
      }
    },
    onTimeChange(event) {
      this.form.wakeTargetTime = event.detail.value
    },
    onNotificationChange(event) {
      this.form.notificationEnabled = event.detail.value
    },
    async handleSave() {
      this.setting = await updateSettings(this.form)
      this.form = {
        wakeTargetTime: this.setting.wakeTargetTime,
        notificationEnabled: !!this.setting.notificationEnabled
      }
      uni.showToast({ title: '设置已保存', icon: 'success' })
    },
    async handleChangePassword() {
      if (this.passwordSubmitting) {
        return
      }
      if (!this.passwordForm.oldPassword || !this.passwordForm.newPassword || !this.passwordForm.confirmPassword) {
        uni.showToast({ title: '请完整填写密码信息', icon: 'none' })
        return
      }
      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        uni.showToast({ title: '两次输入的新密码不一致', icon: 'none' })
        return
      }

      try {
        this.passwordSubmitting = true
        await changePassword(this.passwordForm)
        this.passwordForm = {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
        uni.showToast({ title: '密码已更新', icon: 'success' })
      } catch (error) {
        uni.showToast({ title: error.message || '密码修改失败', icon: 'none' })
      } finally {
        this.passwordSubmitting = false
      }
    },
    handleLogout() {
      logout()
      this.authUser = null
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      uni.showToast({ title: '已退出登录', icon: 'success' })
      setTimeout(() => {
        uni.navigateTo({ url: `/pages/login/index?redirect=${encodeURIComponent('/pages/mine/index')}` })
      }, 500)
    }
  }
}
</script>

<style scoped>
.mine-banner {
  margin-bottom: 24rpx;
  background:
    radial-gradient(circle at top right, rgba(255, 244, 232, 0.24), transparent 24%),
    linear-gradient(135deg, #4d3631, #8d573e 58%, #ddb078);
  color: #fff;
}

.stage-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.stage-kicker {
  background: rgba(255, 248, 240, 0.16);
  color: #fff5ed;
  border-color: rgba(255, 245, 237, 0.18);
}

.stage-note {
  font-size: 22rpx;
  line-height: 1.6;
  color: rgba(255, 244, 235, 0.82);
}

.banner-title {
  font-size: 48rpx;
  font-weight: 700;
  line-height: 1.24;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.banner-subtitle {
  margin-top: 12rpx;
  font-size: 26rpx;
  line-height: 1.78;
  opacity: 0.92;
}

.stage-strip {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
  margin-top: 24rpx;
}

.stage-metric {
  padding: 18rpx 18rpx 20rpx;
  border-radius: 22rpx;
  background: rgba(255, 248, 241, 0.14);
  border: 1rpx solid rgba(255, 245, 237, 0.16);
}

.stage-metric-label {
  font-size: 20rpx;
  letter-spacing: 2rpx;
  color: rgba(255, 245, 237, 0.76);
}

.stage-metric-value {
  margin-top: 10rpx;
  font-size: 32rpx;
  font-weight: 700;
  color: #fff8f2;
}

.profile-card {
  display: flex;
  align-items: flex-start;
  gap: 24rpx;
  margin-bottom: 24rpx;
  background: linear-gradient(180deg, rgba(255, 252, 248, 0.98), rgba(255, 247, 239, 0.95));
}

.profile-aside {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.avatar-wrap {
  padding: 10rpx;
  border-radius: 34rpx;
  background: linear-gradient(135deg, rgba(242, 140, 69, 0.18), rgba(255, 216, 156, 0.22));
}

.avatar {
  width: 108rpx;
  height: 108rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #df6f2d, #f28c45);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  font-weight: 700;
}

.profile-main {
  flex: 1;
  min-width: 0;
}

.profile-overline {
  color: #a06d4c;
  font-size: 20rpx;
  font-weight: 700;
  letter-spacing: 3rpx;
  text-transform: uppercase;
  text-align: center;
}

.name {
  font-size: 40rpx;
  font-weight: 700;
  color: #352a24;
  line-height: 1.25;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.desc,
.setting-row {
  color: #7c726a;
}

.desc {
  margin-top: 12rpx;
  line-height: 1.78;
}

.profile-tags {
  display: flex;
  gap: 12rpx;
  flex-wrap: wrap;
  margin-top: 18rpx;
}

.profile-meta-grid {
  display: grid;
  gap: 14rpx;
  margin-top: 22rpx;
}

.meta-item {
  padding: 18rpx 20rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 1rpx solid rgba(226, 210, 193, 0.6);
}

.meta-label {
  color: #9c7d65;
  font-size: 20rpx;
  letter-spacing: 2rpx;
  text-transform: uppercase;
}

.meta-value {
  margin-top: 8rpx;
  color: #3d312b;
  font-size: 26rpx;
  font-weight: 600;
  line-height: 1.6;
}

.account-value {
  color: #7b4c37;
}

.settings-sheet {
  background: linear-gradient(180deg, rgba(255, 252, 248, 0.98), rgba(248, 243, 237, 0.96));
}

.auth-card,
.password-card,
.settings-card {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.auth-card {
  margin-bottom: 24rpx;
}

.password-card {
  margin-bottom: 24rpx;
}

.sheet-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.sheet-kicker {
  margin-bottom: 14rpx;
}

.sheet-badge {
  flex-shrink: 0;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(216, 165, 90, 0.12);
  color: #7b533c;
  font-size: 22rpx;
  font-weight: 700;
}

.password-form {
  display: grid;
  gap: 16rpx;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.form-label {
  color: #8a6249;
  font-size: 22rpx;
  font-weight: 700;
  letter-spacing: 2rpx;
}

.password-input-field {
  height: 88rpx;
  padding: 0 24rpx;
  background: linear-gradient(180deg, #fffaf7, #fff3eb);
  border-radius: 18rpx;
  border: 1rpx solid rgba(233, 222, 211, 0.9);
  color: #5f5148;
}

.auth-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.auth-btn {
  flex: 1;
  min-width: 0;
}

.secondary-btn {
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: 999rpx;
  background: rgba(122, 111, 102, 0.1);
  color: #6e6259;
  font-weight: 600;
  border: 1rpx solid rgba(160, 139, 120, 0.18);
}

.section-no-margin {
  margin-bottom: 0;
}

.setting-desc {
  color: #8a7b70;
  font-size: 24rpx;
  line-height: 1.7;
}

.setting-picker {
  min-height: 92rpx;
  padding: 22rpx 24rpx;
  background: linear-gradient(180deg, #fffaf7, #fff3eb);
  border-radius: 18rpx;
  color: #374151;
  border: 1rpx solid rgba(233, 222, 211, 0.9);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.picker-label {
  color: #3d312b;
  font-size: 28rpx;
  font-weight: 600;
}

.picker-hint,
.toggle-hint {
  margin-top: 8rpx;
  color: #8a7b70;
  font-size: 22rpx;
  line-height: 1.6;
}

.picker-value {
  flex-shrink: 0;
  font-size: 34rpx;
  font-weight: 700;
  color: #8f5535;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.toggle-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  padding: 16rpx 0 10rpx;
}
</style>
