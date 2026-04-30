<template>
  <view class="page">
    <view class="card login-banner login-stage">
      <view class="banner-topline">
        <view class="banner-badge editorial-kicker">
          <brand-icon :name="isRegisterMode ? 'profile' : 'quote'" :size="22" />
          <text>{{ isRegisterMode ? '注册' : '登录' }}</text>
        </view>
        <view class="banner-side-note">{{ isRegisterMode ? '新页准备好' : '演示账号可用' }}</view>
      </view>
      <view class="banner-title">{{ isRegisterMode ? '开一页属于你的生活账本。' : '回到你的生活账本。' }}</view>
      <view class="banner-subtitle">{{ isRegisterMode ? '注册成功后，今天的记录会从首页开始。' : '登录后继续查看美食、签到和设置。' }}</view>
      <view class="banner-note demo-pill">演示账号：demo / 123456</view>
      <view class="auth-mode-switch">
        <view class="mode-pill" :class="{ active: !isRegisterMode }" @tap="switchMode('login')">登录</view>
        <view class="mode-pill" :class="{ active: isRegisterMode }" @tap="switchMode('register')">注册</view>
      </view>
    </view>

    <view class="card login-card credential-card">
      <view class="editorial-kicker form-kicker">
        <brand-icon :name="isRegisterMode ? 'profile' : 'key'" :size="22" />
        <text>{{ isRegisterMode ? '新页信息' : '账号信息' }}</text>
      </view>
      <view class="section-title section-no-margin">{{ isRegisterMode ? '注册账号' : '账号登录' }}</view>
      <view class="section-subtext form-tip">{{ isRegisterMode ? '用户名用来登录，昵称会出现在页面上。' : '输入账号和密码，接着写今天这一页。' }}</view>
      <view class="field-group">
        <view class="field-label">用户名</view>
        <input v-model="form.username" class="input" placeholder="请输入用户名" />
      </view>
      <view v-if="isRegisterMode" class="field-group register-panel">
        <view class="field-label">昵称</view>
        <input v-model="form.nickname" class="input" placeholder="请输入昵称" />
      </view>
      <view class="field-group">
        <view class="field-label">密码</view>
        <view class="password-field">
          <input v-model="form.password" class="input password-input" :password="!showPassword" placeholder="请输入密码" />
          <view class="password-toggle" @tap="togglePassword">{{ showPassword ? '隐藏' : '显示' }}</view>
        </view>
      </view>
      <view v-if="isRegisterMode" class="field-group confirm-password-field">
        <view class="field-label">确认密码</view>
        <input v-model="form.confirmPassword" class="input confirm-input" :password="!showPassword" placeholder="再输入一次密码" />
      </view>
      <view class="primary-btn" :class="{ disabled: submitting }" @tap="handleSubmit">{{ submitting ? submitLoadingText : submitButtonText }}</view>
      <view class="login-tip">{{ submitTip }}</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { getCurrentUser, login, register } from '../../api/auth'
import { clearSession, getToken, setAuthUser, setToken } from '../../utils/auth'

export default {
  components: {
    BrandIcon
  },
  data() {
    return {
      forceLogin: false,
      mode: 'login',
      checkingSession: false,
      submitting: false,
      showPassword: false,
      form: {
        username: '',
        nickname: '',
        password: '',
        confirmPassword: ''
      }
    }
  },
  computed: {
    isRegisterMode() {
      return this.mode === 'register'
    },
    submitButtonText() {
      return this.isRegisterMode ? '注册进入' : '登录进入'
    },
    submitLoadingText() {
      return this.isRegisterMode ? '正在注册...' : '正在登录...'
    },
    submitTip() {
      return this.isRegisterMode
        ? '注册成功后回到首页。'
        : '登录成功后回到首页。'
    }
  },
  onLoad(options) {
    if (options && options.force === 'true') {
      this.forceLogin = true
    }
  },
  onShow() {
    this.validateCurrentSession()
  },
  methods: {
    async validateCurrentSession() {
      if (this.forceLogin || this.checkingSession || !getToken()) {
        return
      }

      try {
        this.checkingSession = true
        const user = await getCurrentUser()
        setAuthUser(user)
        this.navigateAfterLogin()
      } catch (error) {
        clearSession()
      } finally {
        this.checkingSession = false
      }
    },
    switchMode(mode) {
      if (this.submitting || this.mode === mode) {
        return
      }
      this.mode = mode
      this.form.password = ''
      this.form.confirmPassword = ''
    },
    togglePassword() {
      this.showPassword = !this.showPassword
    },
    async handleSubmit() {
      if (this.submitting) {
        return
      }
      if (this.isRegisterMode) {
        await this.handleRegister()
        return
      }
      await this.handleLogin()
    },
    async handleLogin() {
      if (!this.form.username || !this.form.password) {
        uni.showToast({ title: '先填写用户名和密码', icon: 'none' })
        return
      }

      try {
        this.submitting = true
        const result = await login({
          username: this.form.username,
          password: this.form.password
        })
        this.finishAuth(result, '已登录')
      } catch (error) {
        uni.showToast({ title: error.message || '登录失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    },
    async handleRegister() {
      if (!this.form.username || !this.form.nickname || !this.form.password || !this.form.confirmPassword) {
        uni.showToast({ title: '注册信息还没填完整', icon: 'none' })
        return
      }
      if (this.form.password !== this.form.confirmPassword) {
        uni.showToast({ title: '两次密码不一致', icon: 'none' })
        return
      }

      try {
        this.submitting = true
        const result = await register({
          username: this.form.username,
          nickname: this.form.nickname,
          password: this.form.password,
          confirmPassword: this.form.confirmPassword
        })
        this.finishAuth(result, '已注册')
      } catch (error) {
        uni.showToast({ title: error.message || '注册失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    },
    finishAuth(result, message) {
      setToken(result.token)
      setAuthUser(result.user)
      this.forceLogin = false
      uni.showToast({ title: message, icon: 'success' })
      setTimeout(() => {
        this.navigateAfterLogin()
      }, 500)
    },
    navigateAfterLogin() {
      uni.switchTab({ url: '/pages/index/index' })
    }
  }
}
</script>

<style scoped>
.login-banner {
  margin-bottom: 24rpx;
  background:
    radial-gradient(circle at top right, rgba(255, 246, 235, 0.26), transparent 24%),
    linear-gradient(135deg, #47342f, #8a523d 58%, #ddb078);
  color: #fff;
}

.banner-badge {
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 10rpx;
  white-space: nowrap;
  background: rgba(255, 247, 238, 0.16);
  color: #fff6ef;
  border-color: rgba(255, 245, 236, 0.18);
}

.banner-badge text,
.form-kicker text {
  white-space: nowrap;
}

.banner-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.banner-side-note {
  font-size: 22rpx;
  line-height: 1.6;
  color: rgba(255, 246, 237, 0.84);
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
}

.banner-note {
  margin-top: 22rpx;
  display: inline-flex;
  align-self: flex-start;
}

.auth-mode-switch {
  display: inline-flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 24rpx;
  padding: 10rpx;
  border-radius: 999rpx;
  background: rgba(255, 248, 242, 0.12);
  border: 1rpx solid rgba(255, 245, 236, 0.16);
}

.mode-pill {
  min-width: 120rpx;
  padding: 12rpx 24rpx;
  border-radius: 999rpx;
  text-align: center;
  color: rgba(255, 248, 241, 0.76);
  font-size: 22rpx;
  font-weight: 600;
}

.mode-pill.active {
  background: rgba(255, 251, 246, 0.92);
  color: #7d4f39;
}

.demo-pill {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 248, 242, 0.14);
  color: #fff8f3;
  border: 1rpx solid rgba(255, 245, 236, 0.18);
  font-size: 22rpx;
  line-height: 1.6;
}

.login-card {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  background: linear-gradient(180deg, rgba(255, 252, 248, 0.98), rgba(248, 243, 237, 0.96));
}

.section-no-margin {
  margin-bottom: 0;
}

.form-kicker {
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 10rpx;
  white-space: nowrap;
  margin-bottom: 14rpx;
}

.form-tip {
  margin-top: 10rpx;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.register-panel,
.confirm-password-field {
  min-width: 0;
}

.field-label {
  color: #8a6249;
  font-size: 22rpx;
  font-weight: 700;
  letter-spacing: 2rpx;
}

.password-field {
  position: relative;
}

.input {
  height: 88rpx;
  padding: 0 24rpx;
  background: linear-gradient(180deg, #fffaf7, #fff3eb);
  border-radius: 18rpx;
  border: 1rpx solid rgba(233, 222, 211, 0.9);
  color: #5f5148;
}

.password-input {
  padding-right: 112rpx;
}

.password-toggle {
  position: absolute;
  right: 24rpx;
  top: 50%;
  transform: translateY(-50%);
  color: #b56d43;
  font-size: 24rpx;
  font-weight: 600;
}

.login-tip {
  margin-top: 2rpx;
  color: #97877a;
  font-size: 22rpx;
  line-height: 1.6;
}

.disabled {
  opacity: 0.7;
}
</style>
