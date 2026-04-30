<template>
  <view class="page">
    <view class="card password-hero password-sheet">
      <view class="hero-topline">
        <view class="editorial-kicker hero-kicker">
          <brand-icon name="warning" :size="26" />
          <text>账号安全</text>
        </view>
        <view class="hero-note">偶尔换一把钥匙</view>
      </view>
      <view class="hero-title">修改密码</view>
      <view class="hero-subtitle">先输入旧密码，再把新密码收好。下次登录就用新的。</view>
    </view>

    <view class="card password-card settings-sheet">
      <view class="section-title section-no-margin">账号安全</view>
      <view class="section-subtext form-tip">新密码写两遍，确认没有手误。</view>
      <view class="password-form">
        <view class="form-field">
          <view class="form-label form-label-with-icon">
            <brand-icon name="key" :size="22" />
            <text>旧密码</text>
          </view>
          <input v-model="passwordForm.oldPassword" class="input-field" password placeholder="请输入旧密码" />
        </view>
        <view class="form-field">
          <view class="form-label form-label-with-icon">
            <brand-icon name="save" :size="22" />
            <text>新密码</text>
          </view>
          <input v-model="passwordForm.newPassword" class="input-field" password placeholder="请输入新密码" />
        </view>
        <view class="form-field">
          <view class="form-label form-label-with-icon">
            <brand-icon name="success" :size="22" />
            <text>确认新密码</text>
          </view>
          <input v-model="passwordForm.confirmPassword" class="input-field" password placeholder="再输入一次新密码" />
        </view>
      </view>
      <view class="primary-btn" :class="{ disabled: passwordSubmitting }" @tap="handleChangePassword">
        {{ passwordSubmitting ? '正在提交...' : '保存新密码' }}
      </view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { changePassword } from '../../api/auth'
import { getToken } from '../../utils/auth'

export default {
  components: {
    BrandIcon
  },
  data() {
    return {
      passwordSubmitting: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    }
  },
  onShow() {
    this.ensureLoggedIn()
  },
  methods: {
    ensureLoggedIn() {
      if (getToken()) {
        return true
      }

      uni.navigateTo({ url: `/pages/login/index?redirect=${encodeURIComponent('/pages/mine/password')}` })
      return false
    },
    async handleChangePassword() {
      if (this.passwordSubmitting) {
        return
      }
      if (!this.passwordForm.oldPassword || !this.passwordForm.newPassword || !this.passwordForm.confirmPassword) {
        uni.showToast({ title: '密码信息还没填完整', icon: 'none' })
        return
      }
      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        uni.showToast({ title: '两次新密码不一致', icon: 'none' })
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
        uni.showToast({ title: '密码已保存', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 500)
      } catch (error) {
        uni.showToast({ title: error.message || '密码没修改成功', icon: 'none' })
      } finally {
        this.passwordSubmitting = false
      }
    }
  }
}
</script>

<style scoped>
.password-hero {
  margin-bottom: 24rpx;
  background:
    radial-gradient(circle at top right, rgba(255, 244, 232, 0.24), transparent 24%),
    linear-gradient(135deg, #4d3631, #8d573e 58%, #ddb078);
  color: #fff;
}

.hero-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 20rpx;
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 10rpx;
  white-space: nowrap;
  background: rgba(255, 248, 240, 0.16);
  color: #fff5ed;
  border-color: rgba(255, 245, 237, 0.18);
}

.hero-kicker text,
.form-label-with-icon text {
  white-space: nowrap;
}

.hero-note {
  color: rgba(255, 244, 235, 0.82);
  font-size: 22rpx;
}

.hero-title {
  font-size: 48rpx;
  font-weight: 700;
  line-height: 1.24;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.hero-subtitle {
  margin-top: 12rpx;
  color: rgba(255, 250, 245, 0.92);
  font-size: 26rpx;
  line-height: 1.78;
}

.settings-sheet {
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.58), transparent 18%),
    linear-gradient(180deg, rgba(255, 252, 248, 0.98), rgba(248, 243, 237, 0.96));
}

.section-no-margin {
  margin-bottom: 0;
}

.form-tip {
  margin-top: 10rpx;
}

.password-form {
  display: grid;
  gap: 16rpx;
  margin: 24rpx 0;
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

.form-label-with-icon {
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 10rpx;
  white-space: nowrap;
}

.input-field {
  height: 88rpx;
  padding: 0 24rpx;
  background: linear-gradient(180deg, #fffaf7, #fff3eb);
  border-radius: 18rpx;
  border: 1rpx solid rgba(233, 222, 211, 0.9);
  color: #5f5148;
}

.disabled {
  opacity: 0.68;
}
</style>
