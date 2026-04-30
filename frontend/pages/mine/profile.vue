<template>
  <view class="page profile-page">
    <view class="card profile-hero profile-sheet profile-color-refresh">
      <view class="hero-topline">
        <view class="editorial-kicker hero-kicker">
          <brand-icon name="profile" :size="26" />
          <text>个人资料</text>
        </view>
        <view class="hero-note">头像小册</view>
      </view>
      <view class="profile-head">
        <view class="avatar-wrap">
          <image v-if="avatarUrl" class="avatar-image" :src="avatarUrl" mode="aspectFill" />
          <view v-else class="avatar">{{ avatarLetter }}</view>
        </view>
        <view class="profile-main">
          <view class="hero-title">{{ userName }}</view>
          <view class="hero-subtitle account-value">@{{ userAccount }}</view>
        </view>
      </view>
    </view>

    <view class="card profile-card profile-sheet">
      <view class="section-title section-no-margin">个人资料</view>
      <view class="section-subtext form-tip">给这本小账本换个署名，也换一张封面。</view>
      <view class="profile-edit-form">
        <view class="form-field">
          <view class="form-label form-label-with-icon">
            <brand-icon name="profile" :size="22" />
            <text>昵称</text>
          </view>
          <input v-model="profileForm.nickname" class="input-field" placeholder="请输入昵称" />
        </view>

        <view class="form-field">
          <view class="form-label form-label-with-icon">
            <brand-icon name="petal" :size="22" />
            <text>头像</text>
          </view>
          <view class="avatar-options profile-avatar-options">
            <view
              class="avatar-option default-avatar-option"
              :class="{ active: !profileForm.avatar }"
              @tap="useDefaultAvatar"
            >
              <view class="avatar-option-default">{{ avatarLetter }}</view>
              <view class="avatar-option-label">默认头像</view>
            </view>
            <view
              v-for="item in avatarOptions"
              :key="item.value"
              class="avatar-option"
              :class="{ active: isAvatarSelected(item.value) }"
              @tap="selectAvatar(item.value)"
            >
              <image class="avatar-option-image" :src="item.value" mode="aspectFit" />
              <view class="avatar-option-label">{{ item.label }}</view>
            </view>
          </view>
        </view>
      </view>

      <view class="primary-btn" :class="{ disabled: profileSubmitting }" @tap="handleSaveProfile">
        {{ profileSubmitting ? '正在保存...' : '保存' }}
      </view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { getCurrentUser, updateProfile } from '../../api/auth'
import { getAuthUser, getToken, setAuthUser } from '../../utils/auth'

export default {
  components: {
    BrandIcon
  },
  data() {
    return {
      authUser: null,
      profileSubmitting: false,
      profileForm: {
        nickname: '',
        avatar: ''
      },
      avatarOptions: [
        { label: '晨光', value: '/static/profile-avatars/avatar-sun.png' },
        { label: '月影', value: '/static/profile-avatars/avatar-moon.png' },
        { label: '花食', value: '/static/profile-avatars/avatar-flower.png' },
        { label: '星标', value: '/static/profile-avatars/avatar-star.png' },
        { label: '云签', value: '/static/profile-avatars/avatar-cloud.png' },
        { label: '小屋', value: '/static/profile-avatars/avatar-leaf.png' },
        { label: '幼芽', value: '/static/profile-avatars/avatar-seed.png' },
        { label: '饭团', value: '/static/profile-avatars/avatar-rice.png' },
        { label: '热茶', value: '/static/profile-avatars/avatar-tea.png' },
        { label: '小书', value: '/static/profile-avatars/avatar-book.png' },
        { label: '远山', value: '/static/profile-avatars/avatar-mountain.png' },
        { label: '雨点', value: '/static/profile-avatars/avatar-rain.png' },
        { label: '彩虹', value: '/static/profile-avatars/avatar-rainbow.png' },
        { label: '吐司', value: '/static/profile-avatars/avatar-toast.png' },
        { label: '晨钟', value: '/static/profile-avatars/avatar-clock.png' },
        { label: '暖心', value: '/static/profile-avatars/avatar-heart.png' },
        { label: '浆果', value: '/static/profile-avatars/avatar-berry.png' },
        { label: '麦穗', value: '/static/profile-avatars/avatar-wheat.png' }
      ]
    }
  },
  computed: {
    userName() {
      return this.authUser && this.authUser.nickname ? this.authUser.nickname : '奇幻妙妙屋用户'
    },
    userAccount() {
      return this.authUser && this.authUser.username ? this.authUser.username : '--'
    },
    avatarUrl() {
      return this.profileForm.avatar || ''
    },
    avatarLetter() {
      const name = (this.profileForm.nickname || this.userName || '').trim()
      return name ? name.charAt(0).toUpperCase() : 'L'
    }
  },
  onShow() {
    if (!this.ensureLoggedIn()) {
      return
    }
    this.authUser = getAuthUser()
    this.syncProfileForm()
    this.loadProfile()
  },
  methods: {
    ensureLoggedIn() {
      if (getToken()) {
        return true
      }

      uni.navigateTo({ url: `/pages/login/index?redirect=${encodeURIComponent('/pages/mine/profile')}` })
      return false
    },
    async loadProfile() {
      try {
        const user = await getCurrentUser()
        this.authUser = user
        setAuthUser(user)
        this.syncProfileForm()
      } catch (error) {
        uni.showToast({ title: error.message || '资料加载失败', icon: 'none' })
      }
    },
    syncProfileForm() {
      this.profileForm = {
        nickname: this.authUser && this.authUser.nickname ? this.authUser.nickname : '',
        avatar: this.authUser && this.authUser.avatar ? this.authUser.avatar : ''
      }
    },
    selectAvatar(value) {
      this.profileForm.avatar = value
    },
    useDefaultAvatar() {
      this.profileForm.avatar = ''
    },
    isAvatarSelected(value) {
      return this.profileForm.avatar === value
    },
    async handleSaveProfile() {
      if (this.profileSubmitting) {
        return
      }
      const nickname = this.profileForm.nickname ? this.profileForm.nickname.trim() : ''
      if (!nickname) {
        uni.showToast({ title: '先填写昵称', icon: 'none' })
        return
      }

      try {
        this.profileSubmitting = true
        const user = await updateProfile({
          nickname,
          avatar: this.profileForm.avatar
        })
        this.authUser = user
        setAuthUser(user)
        this.syncProfileForm()
        uni.showToast({ title: '资料已保存', icon: 'success' })
      } catch (error) {
        uni.showToast({ title: error.message || '资料没保存成功', icon: 'none' })
      } finally {
        this.profileSubmitting = false
      }
    }
  }
}
</script>

<style scoped>
.profile-hero {
  margin-bottom: 24rpx;
  background:
    radial-gradient(circle at top right, rgba(255, 238, 232, 0.28), transparent 24%),
    linear-gradient(135deg, #375f8f, #4d92a8 58%, #f08a6a);
  color: #fff;
}

.profile-sheet {
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.62), transparent 18%),
    linear-gradient(180deg, rgba(255, 180, 49, 1.0), rgba(250, 201, 173, 1.0));
}

.hero-topline,
.profile-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}

.hero-topline {
  margin-bottom: 26rpx;
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 10rpx;
  white-space: nowrap;
  background: rgba(247, 252, 255, 0.18);
  color: #f8fcff;
  border-color: rgba(247, 252, 255, 0.2);
}

.hero-kicker text,
.form-label-with-icon text {
  white-space: nowrap;
  word-break: keep-all;
}

.hero-note {
  color: rgba(248, 252, 255, 0.86);
  font-size: 22rpx;
}

.avatar-wrap {
  flex-shrink: 0;
  padding: 10rpx;
  border-radius: 34rpx;
  background: linear-gradient(135deg, rgba(147, 152, 255, 0.26), rgba(55, 218, 132, 0.24));
}

.avatar,
.avatar-image {
  width: 128rpx;
  height: 128rpx;
  border-radius: 32rpx;
}

.avatar {
  background: linear-gradient(135deg, #375f8f, #f08a6a);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  font-weight: 700;
}

.avatar-image {
  display: block;
  background: #eef7fa;
}

.profile-main {
  flex: 1;
  min-width: 0;
}

.hero-title {
  font-size: 48rpx;
  font-weight: 700;
  line-height: 1.24;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hero-subtitle {
  margin-top: 10rpx;
  color: rgba(248, 252, 255, 0.92);
  font-size: 26rpx;
  line-height: 1.6;
}

.section-no-margin {
  margin-bottom: 0;
}

.form-tip {
  margin-top: 10rpx;
}

.profile-edit-form {
  display: grid;
  gap: 24rpx;
  margin: 24rpx 0;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.form-label {
  color: #3b6686;
  font-size: 22rpx;
  font-weight: 700;
  letter-spacing: 2rpx;
}

.form-label-with-icon {
  display: inline-flex;
  align-items: center;
  flex-direction: row;
  flex-wrap: nowrap;
  gap: 10rpx;
  align-self: flex-start;
  width: auto;
  max-width: 100%;
  white-space: nowrap;
}

.input-field {
  height: 88rpx;
  padding: 0 24rpx;
  background: linear-gradient(180deg, #fbfdff, #edf7fa);
  border-radius: 18rpx;
  border: 1rpx solid rgba(77, 146, 168, 0.18);
  color: #31495d;
}

.avatar-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14rpx;
}

.avatar-option {
  min-width: 0;
  padding: 18rpx 12rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.74);
  border: 2rpx solid rgba(77, 146, 168, 0.12);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.avatar-option.active {
  border-color: #4d92a8;
  background: linear-gradient(180deg, rgba(242, 250, 253, 0.98), rgba(255, 240, 234, 0.86));
  box-shadow: 0 16rpx 28rpx rgba(55, 95, 143, 0.1);
}

.avatar-option-image,
.avatar-option-default {
  width: 82rpx;
  height: 82rpx;
  border-radius: 24rpx;
}

.avatar-option-image {
  display: block;
}

.avatar-option-default {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  font-weight: 800;
  background: linear-gradient(135deg, #375f8f, #f08a6a);
}

.avatar-option-label {
  max-width: 100%;
  color: #35546b;
  font-size: 22rpx;
  font-weight: 700;
  line-height: 1.35;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.disabled {
  opacity: 0.68;
}

@media screen and (max-width: 380px) {
  .profile-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .avatar-options {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
