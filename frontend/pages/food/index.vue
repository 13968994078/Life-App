<template>
  <view class="page food-page">
    <view class="card food-hero">
      <view class="hero-topline">
        <view class="section-label">
          <brand-icon name="food" :size="24" />
          <text>本期饭点</text>
        </view>
        <view class="hero-pill">{{ selectedCategory === '全部' ? '全部分类' : selectedCategory }}</view>
      </view>
      <view class="hero-title">{{ content.title }}</view>
      <view class="hero-subtitle">{{ content.subtitle }}</view>
      <view class="hero-summary">
        <view class="hero-summary-item">
          <view class="hero-summary-label">候选菜单</view>
          <view class="hero-summary-value">{{ wheelItems.length }} 项</view>
        </view>
        <view class="hero-summary-item">
          <view class="hero-summary-label">上次记录</view>
          <view class="hero-summary-value">{{ highlightedFoodName || '还没开始' }}</view>
        </view>
      </view>
    </view>

    <view class="card toolbar-card food-layout-card">
      <view>
        <view class="section-title">先定一个口味范围</view>
        <view class="section-subtext">范围小一点，转盘才像真的懂你。</view>
      </view>
      <picker :range="categories" :disabled="spinning" @change="onCategoryChange">
        <view class="picker-surface" :class="{ disabled: spinning }">当前分类：{{ selectedCategory || '全部' }}</view>
      </picker>
    </view>

    <food-wheel
      :items="wheelItems"
      :result="resultText"
      :rotation-deg="wheelRotation"
      :spinning="spinning"
      :highlighted-item="highlightedFoodName"
      @spin="handleSpin"
    />

    <view class="card form-card food-layout-card">
      <view class="section-label">
        <brand-icon name="add" :size="24" />
        <text>菜单编辑</text>
      </view>
      <view class="section-title form-title">{{ editingId ? '改一条菜单' : '写下一个想吃的' }}</view>
      <view class="section-subtext">{{ editingId ? '保存后会参与下一次抽取。' : '常吃的、突然想试的，都可以先收着。' }}</view>
      <input v-model="form.name" class="input" placeholder="填个美食名" />
      <picker :range="categories.slice(1)" :disabled="spinning" @change="onFormCategoryChange">
        <view class="picker-surface" :class="{ disabled: spinning }">选择分类：{{ form.category || '请选择' }}</view>
      </picker>
      <input v-model="form.priceRange" class="input" placeholder="价格区间，如 20-30" />
      <view class="form-actions">
        <view v-if="editingId" class="secondary-btn" @tap="resetForm">取消编辑</view>
        <view class="primary-btn form-submit" :class="{ disabled: spinning }" @tap="handleSubmit">{{ editingId ? '保存修改' : '保存' }}</view>
      </view>
    </view>

    <view class="card list-card food-layout-card">
      <view class="list-head">
        <view>
          <view class="section-label">
            <brand-icon name="list" :size="24" />
            <text>这一页菜单</text>
          </view>
          <view class="section-title list-title">{{ foods.length ? '这些名字都在转盘里' : '先写几样常吃的' }}</view>
        </view>
        <view class="soft-chip">{{ foods.length }} 项</view>
      </view>
      <view v-for="item in foods" :key="item.id" class="food-row">
        <view class="food-main">
          <view class="food-name">{{ item.name }}</view>
          <view class="food-meta">{{ item.category }} · {{ item.priceRange || '价格未填' }}</view>
          <view class="food-creator">
            <image v-if="creatorAvatarUrl(item)" class="creator-avatar-image" :src="creatorAvatarUrl(item)" mode="aspectFill" />
            <view v-else class="creator-avatar-fallback">{{ creatorAvatarLetter(item) }}</view>
            <text class="creator-name">添加人：{{ creatorName(item) }}</text>
          </view>
        </view>
        <view v-if="canManageFood(item)" class="food-actions">
          <view class="text-btn" :class="{ disabled: spinning }" @tap="startEdit(item)">编辑</view>
          <view class="text-btn danger" :class="{ disabled: spinning }" @tap="handleDelete(item)">删除</view>
        </view>
      </view>
      <view v-if="!foods.length" class="empty-state">
        <brand-icon name="empty" :size="42" />
        <view class="empty-text">{{ content.emptyTip }}</view>
      </view>
    </view>

    <view class="card list-card food-layout-card">
      <view class="list-head">
        <view>
          <view class="section-label">
            <brand-icon name="history" :size="24" />
            <text>饭点回放</text>
          </view>
          <view class="section-title list-title">最近三次饭点</view>
        </view>
        <navigator v-if="history.length > historyPreviewCount" class="history-link" url="/pages/food/history">查看全部</navigator>
      </view>
      <view v-for="item in previewHistory" :key="item.id" class="food-row">
        <view class="food-main">
          <view class="food-name">{{ item.foodName }}</view>
          <view class="food-meta">{{ formatDateTime(item.createdAt) }}</view>
        </view>
        <view class="history-dot"></view>
      </view>
      <view v-if="!history.length" class="empty-text">还没有抽取记录，下一次饭点会留在这里。</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import FoodWheel from '../../components/food-wheel.vue'
import { getHomeContent } from '../../api/content'
import { createFood, deleteFood, getFoodHistory, getFoodList, randomFood, updateFood } from '../../api/food'
import { getAuthUser, getToken } from '../../utils/auth'
import { getTargetRotationDeg, normalizeAngle } from '../../utils/wheel-geometry'

function createDefaultFoodContent() {
  return {
    title: '这一餐，让转盘提个醒',
    subtitle: '选个大概方向，剩下的交给一点运气。',
    emptyTip: '菜单还是空的，先写下几样常吃的。'
  }
}

export default {
  components: {
    BrandIcon,
    FoodWheel
  },
  data() {
    return {
      categories: ['全部', '早餐', '午餐', '晚餐', '夜宵'],
      historyPreviewCount: 3,
      selectedCategory: '全部',
      foods: [],
      history: [],
      editingId: null,
      spinning: false,
      wheelRotation: 0,
      highlightedFoodName: '',
      wheelItems: ['早餐', '午餐', '晚餐', '夜宵'],
      resultText: '',
      lastAuthUserId: null,
      content: createDefaultFoodContent(),
      form: {
        name: '',
        category: '',
        priceRange: ''
      }
    }
  },
  computed: {
    previewHistory() {
      return this.history.slice(0, this.historyPreviewCount)
    },
    currentUserId() {
      const authUser = getAuthUser()
      if (!authUser || authUser.id === null || authUser.id === undefined) {
        return null
      }
      return String(authUser.id)
    }
  },
  onShow() {
    if (!this.ensureLoggedIn()) {
      return
    }
    this.resetEditStateForAccount()
    this.loadPageData()
  },
  methods: {
    ensureLoggedIn() {
      if (getToken()) {
        return true
      }

      uni.navigateTo({ url: `/pages/login/index?redirect=${encodeURIComponent('/pages/food/index')}` })
      return false
    },
    async loadPageData() {
      const contentPromise = getHomeContent().then((data) => data.foodHero || createDefaultFoodContent()).catch(() => createDefaultFoodContent())
      try {
        await Promise.all([this.loadFoods(), this.loadHistory()])
      } finally {
        this.content = await contentPromise
      }
    },
    async loadFoods() {
      const category = this.selectedCategory === '全部' ? '' : this.selectedCategory
      this.foods = await getFoodList(category)
      this.validateEditingPermission()
      if (!this.spinning) {
        this.wheelItems = this.buildWheelItems(this.foods, this.highlightedFoodName)
      }
    },
    async loadHistory() {
      this.history = await getFoodHistory()
    },
    async handleSpin() {
      if (this.spinning) {
        return
      }
      const category = this.selectedCategory === '全部' ? '' : this.selectedCategory
      try {
        const result = await randomFood(category)
        this.highlightedFoodName = ''
        this.resultText = '转盘转着...'
        this.wheelItems = this.buildWheelItems(this.foods, result.name)

        const targetIndex = Math.max(this.wheelItems.indexOf(result.name), 0)
        const targetRotation = getTargetRotationDeg(targetIndex, this.wheelItems.length)
        const currentRotation = normalizeAngle(this.wheelRotation)
        const pointerAngle = normalizeAngle(targetRotation - currentRotation)
        const extraTurns = 360 * 5

        this.spinning = true
        this.wheelRotation += extraTurns + pointerAngle

        setTimeout(async () => {
          this.spinning = false
          this.highlightedFoodName = result.name
          this.resultText = `今天吃：${result.name}`
          await this.loadHistory()
        }, 4300)
      } catch (error) {
        this.resultText = error.message
      }
    },
    onCategoryChange(event) {
      if (this.spinning) {
        return
      }
      this.selectedCategory = this.categories[event.detail.value]
      this.loadFoods()
    },
    onFormCategoryChange(event) {
      if (this.spinning) {
        return
      }
      this.form.category = this.categories.slice(1)[event.detail.value]
    },
    async handleSubmit() {
      if (this.spinning) {
        uni.showToast({ title: '转盘还在转，稍等一下', icon: 'none' })
        return
      }
      if (!this.form.name || !this.form.category) {
        uni.showToast({ title: '先填名称和分类', icon: 'none' })
        return
      }
      const isEditing = !!this.editingId
      if (isEditing && !this.canManageFood(this.currentEditingFood())) {
        this.resetForm()
        uni.showToast({ title: '只能修改自己添加的美食', icon: 'none' })
        return
      }
      const payload = {
        name: this.form.name,
        category: this.form.category,
        priceRange: this.form.priceRange,
        tags: []
      }
      if (isEditing) {
        await updateFood(this.editingId, payload)
      } else {
        await createFood(payload)
      }
      this.resetForm()
      uni.showToast({ title: isEditing ? '已修改' : '已保存', icon: 'success' })
      await this.loadFoods()
    },
    startEdit(item) {
      if (this.spinning) {
        return
      }
      if (!this.canManageFood(item)) {
        uni.showToast({ title: '只能编辑自己添加的美食', icon: 'none' })
        return
      }
      this.editingId = item.id
      this.form = {
        name: item.name,
        category: item.category,
        priceRange: item.priceRange || ''
      }
    },
    resetForm() {
      this.editingId = null
      this.form = {
        name: '',
        category: '',
        priceRange: ''
      }
    },
    resetEditStateForAccount() {
      const userId = this.currentUserId
      if (this.lastAuthUserId === userId) {
        return
      }
      this.lastAuthUserId = userId
      this.resetForm()
      this.foods = []
      this.history = []
      this.highlightedFoodName = ''
      this.resultText = ''
      this.wheelItems = ['早餐', '午餐', '晚餐', '夜宵']
    },
    currentEditingFood() {
      if (!this.editingId) {
        return null
      }
      return this.foods.find((item) => item && String(item.id) === String(this.editingId)) || null
    },
    validateEditingPermission() {
      if (this.editingId && !this.canManageFood(this.currentEditingFood())) {
        this.resetForm()
      }
    },
    buildWheelItems(foods, preferredName) {
      const names = (foods || []).map((item) => item.name).filter(Boolean)
      if (!names.length) {
        return ['早餐', '午餐', '晚餐', '夜宵']
      }

      const uniqueNames = [...new Set(names)]
      if (preferredName && !uniqueNames.includes(preferredName)) {
        return [...uniqueNames, preferredName]
      }
      return uniqueNames
    },
    handleDelete(item) {
      if (this.spinning) {
        uni.showToast({ title: '转盘还在转，稍等一下', icon: 'none' })
        return
      }
      if (!this.canManageFood(item)) {
        uni.showToast({ title: '只能删除自己添加的美食', icon: 'none' })
        return
      }
      uni.showModal({
        title: '删除美食',
        content: `确定删除「${item.name}」吗？`,
        success: async (res) => {
          if (!res.confirm) {
            return
          }
          await deleteFood(item.id)
          if (this.editingId === item.id) {
            this.resetForm()
          }
          if (this.highlightedFoodName === item.name) {
            this.highlightedFoodName = ''
            this.resultText = ''
          }
          uni.showToast({ title: '已删除', icon: 'success' })
          await this.loadFoods()
        }
      })
    },
    canManageFood(item) {
      return !!item && !!this.currentUserId && String(item.userId) === this.currentUserId
    },
    creatorName(item) {
      if (!item) {
        return '未命名用户'
      }
      return item.creatorName || `用户${item.userId || ''}`
    },
    creatorAvatarUrl(item) {
      return item && item.creatorAvatar ? item.creatorAvatar : ''
    },
    creatorAvatarLetter(item) {
      const name = this.creatorName(item).trim()
      return name ? name.charAt(0).toUpperCase() : 'L'
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
.food-hero {
  background: linear-gradient(135deg, #5f3e31, #905840 58%, #d6a067);
  color: #fffaf4;
}

.hero-topline,
.list-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.hero-pill {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 248, 241, 0.16);
  font-size: 22rpx;
}

.hero-title {
  margin-top: 18rpx;
  font-size: 50rpx;
  line-height: 1.22;
  font-weight: 700;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.hero-subtitle {
  margin-top: 14rpx;
  font-size: 26rpx;
  line-height: 1.74;
  color: rgba(255, 246, 236, 0.9);
}

.hero-summary {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
  margin-top: 24rpx;
}

.hero-summary-item {
  padding: 18rpx;
  border-radius: 22rpx;
  background: rgba(255, 248, 241, 0.14);
}

.hero-summary-label {
  font-size: 22rpx;
  color: rgba(255, 245, 237, 0.82);
}

.hero-summary-value {
  margin-top: 10rpx;
  font-size: 30rpx;
  font-weight: 700;
}

.toolbar-card,
.form-card,
.list-card {
  margin-top: 24rpx;
}

.food-layout-card {
  background: linear-gradient(180deg, rgba(255, 252, 247, 0.98), rgba(255, 247, 239, 0.95));
}

.toolbar-card .picker-surface,
.form-card .picker-surface {
  margin-top: 20rpx;
}

.form-title,
.list-title {
  margin-top: 16rpx;
}

.form-card .input,
.form-card .picker-surface {
  margin-top: 18rpx;
}

.form-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 22rpx;
}

.form-submit {
  flex: 1;
}

.food-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid rgba(118, 86, 66, 0.12);
}

.food-row:last-child {
  border-bottom: 0;
}

.food-main {
  flex: 1;
  min-width: 0;
}

.food-name {
  color: #2c201b;
  font-size: 30rpx;
  font-weight: 700;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.food-meta {
  margin-top: 8rpx;
  color: #7b675c;
  font-size: 22rpx;
}

.food-creator {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin-top: 12rpx;
  min-width: 0;
}

.creator-avatar-image,
.creator-avatar-fallback {
  width: 38rpx;
  height: 38rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.creator-avatar-image {
  display: block;
  background: #f5e7da;
}

.creator-avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #df6f2d, #f28c45);
  color: #fff;
  font-size: 18rpx;
  font-weight: 800;
}

.creator-name {
  min-width: 0;
  color: #9a7b69;
  font-size: 22rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.food-actions {
  display: flex;
  align-items: center;
  gap: 14rpx;
}

.text-btn {
  color: #715d52;
  font-size: 24rpx;
}

.danger {
  color: #b04f40;
}

.history-link {
  color: #7a4d46;
  font-size: 24rpx;
  font-weight: 600;
}

.history-dot {
  width: 18rpx;
  height: 18rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #a85a33, #e5b36e);
  box-shadow: 0 0 0 10rpx rgba(215, 165, 90, 0.12);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 8rpx;
}

@media screen and (max-width: 380px) {
  .hero-summary {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
