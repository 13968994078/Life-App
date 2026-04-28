<template>
    <view class="page">
      <view class="card top-banner stage-card">
        <view class="stage-topline">
          <view class="editorial-kicker">
            <brand-icon name="food" :size="26" />
            Food Issue
          </view>
          <view class="banner-chip">热量随缘</view>
        </view>
      <view class="banner-title">随机美食</view>
      <view class="banner-subtitle">今天不想纠结，就让味蕾替你做决定。把选择交给转盘，把余下的精力留给真正想做的事。</view>
      <view class="stage-summary">
        <view class="summary-pill">当前候选 {{ wheelItems.length }} 项</view>
        <view class="summary-copy">当前展示全部可见美食</view>
      </view>
    </view>

      <view class="card filter-card filter-strip">
        <view class="filter-head">
          <view>
            <view class="editorial-kicker strip-kicker">
              <brand-icon name="filter" :size="26" />
              Filter
            </view>
            <view class="section-title section-no-margin">分类筛选</view>
          </view>
          <view class="section-subtext">按分类筛选，当前会展示你现在能看到的全部美食。</view>
        </view>
      <picker :range="categories" :disabled="spinning" @change="onCategoryChange">
        <view class="picker" :class="{ disabled: spinning }">当前分类：{{ selectedCategory || '全部' }}</view>
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

    <view class="card add-card editorial-form-card">
      <view class="editorial-kicker form-kicker">
        <brand-icon name="edit" :size="26" />
        Curate Pool
      </view>
      <view class="section-title section-no-margin">{{ editingId ? '编辑美食' : '新增美食' }}</view>
      <view class="form-tip">{{ editingId ? '改一改名称、分类或价格区间，保存后立即生效。' : '把你常吃又容易忘记的选项加进来，下次转盘更懂你。' }}</view>
      <input v-model="form.name" class="input" placeholder="请输入美食名称" />
      <picker :range="categories.slice(1)" :disabled="spinning" @change="onFormCategoryChange">
        <view class="picker" :class="{ disabled: spinning }">选择分类：{{ form.category || '请选择' }}</view>
      </picker>
      <input v-model="form.priceRange" class="input" placeholder="价格区间，例如 20-30" />
      <view class="form-actions">
        <view class="secondary-btn" v-if="editingId" @tap="resetForm">取消编辑</view>
        <view class="primary-btn form-submit" :class="{ disabled: spinning }" @tap="handleSubmit">{{ editingId ? '保存修改' : '保存美食' }}</view>
      </view>
    </view>

    <view class="card list-card editorial-list-card">
      <view class="editorial-kicker list-kicker">
        <brand-icon name="list" :size="26" />
        Collection
      </view>
      <view class="section-title section-no-margin">美食列表</view>
      <view class="section-subtext list-tip">当前美食池会参与转盘抽取</view>
      <view v-for="item in foods" :key="item.id" class="food-row">
        <view class="food-main">
          <view class="food-name">{{ item.name }}</view>
          <view class="food-meta">{{ item.category }} · {{ item.priceRange || '价格待补充' }}</view>
        </view>
        <view class="food-actions">
          <template v-if="canManageFood(item)">
            <view class="text-btn" :class="{ disabled: spinning }" @tap="startEdit(item)">编辑</view>
            <view class="text-btn danger" :class="{ disabled: spinning }" @tap="handleDelete(item)">删除</view>
          </template>
        </view>
      </view>
      <view v-if="!foods.length" class="empty-text">当前筛选下还没有美食</view>
    </view>

    <view class="card history-card editorial-list-card">
      <view class="history-head">
        <view>
          <view class="editorial-kicker list-kicker">
            <brand-icon name="history" :size="26" />
            Recent Spins
          </view>
          <view class="section-title section-no-margin">最近抽取</view>
          <view class="section-subtext list-tip">默认先看最近几条，完整记录单独展开查看。</view>
        </view>
        <navigator v-if="history.length > historyPreviewCount" class="history-link" url="/pages/food/history">查看全部 {{ history.length }} 条</navigator>
      </view>
      <view v-for="item in previewHistory" :key="item.id" class="food-row">
        <view class="food-main">
          <view class="food-name">{{ item.foodName }}</view>
          <view class="food-meta">{{ formatDateTime(item.createdAt) }}</view>
        </view>
        <view class="history-dot"></view>
      </view>
      <view v-if="!history.length" class="empty-text">还没有抽取记录</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import FoodWheel from '../../components/food-wheel.vue'
import { createFood, deleteFood, getFoodHistory, getFoodList, randomFood, updateFood } from '../../api/food'
import { getAuthUser, getToken } from '../../utils/auth'
import { getTargetRotationDeg, normalizeAngle } from '../../utils/wheel-geometry'

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
      form: {
        name: '',
        category: '',
        priceRange: ''
      }
    }
  },
  computed: {
    foodNames() {
      return this.foods.map((item) => item.name)
    },
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
      await Promise.all([this.loadFoods(), this.loadHistory()])
    },
    async loadFoods() {
      const category = this.selectedCategory === '全部' ? '' : this.selectedCategory
      this.foods = await getFoodList(category)
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
        this.resultText = '转盘旋转中...'
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
        uni.showToast({ title: '转盘进行中，请稍后', icon: 'none' })
        return
      }
      if (!this.form.name || !this.form.category) {
        uni.showToast({ title: '请先填写名称和分类', icon: 'none' })
        return
      }
      const isEditing = !!this.editingId
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
      uni.showToast({ title: isEditing ? '修改成功' : '保存成功', icon: 'success' })
      await this.loadFoods()
    },
    startEdit(item) {
      if (this.spinning) {
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
        uni.showToast({ title: '转盘进行中，请稍后', icon: 'none' })
        return
      }
      uni.showModal({
        title: '删除确认',
        content: `确认删除「${item.name}」吗？`,
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
.top-banner {
  margin-bottom: 24rpx;
  background:
    radial-gradient(circle at top right, rgba(255, 240, 220, 0.28), transparent 24%),
    linear-gradient(135deg, #624132, #925b40 56%, #d59b5d);
  color: #fffdf8;
}

.stage-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.banner-title {
  margin-top: 18rpx;
  font-size: 50rpx;
  font-weight: 700;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.banner-subtitle {
  margin-top: 14rpx;
  font-size: 26rpx;
  line-height: 1.78;
  opacity: 0.94;
}

.banner-chip {
  padding: 12rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 250, 242, 0.16);
  font-size: 22rpx;
  border: 1rpx solid rgba(255, 250, 242, 0.16);
}

.stage-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-top: 24rpx;
}

.summary-pill {
  flex-shrink: 0;
  padding: 12rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 246, 236, 0.18);
  color: #fff8f2;
  font-size: 22rpx;
  font-weight: 600;
  border: 1rpx solid rgba(255, 248, 241, 0.16);
}

.summary-copy {
  text-align: right;
  font-size: 22rpx;
  line-height: 1.6;
  color: rgba(255, 248, 241, 0.84);
}

.filter-card {
  margin-bottom: 24rpx;
}

.filter-strip {
  background:
    linear-gradient(180deg, rgba(255, 251, 246, 0.98), rgba(248, 239, 229, 0.94));
}

.strip-kicker,
.form-kicker,
.list-kicker {
  margin-bottom: 14rpx;
}

.filter-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 18rpx;
  gap: 18rpx;
}

.add-card {
  margin: 24rpx 0;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.editorial-form-card,
.editorial-list-card {
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.58), transparent 20%),
    linear-gradient(180deg, rgba(255, 252, 247, 0.98), rgba(255, 247, 239, 0.95));
}

.form-actions {
  display: flex;
  gap: 16rpx;
}

.form-submit {
  flex: 1;
}

.secondary-btn {
  min-width: 180rpx;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: 999rpx;
  background: rgba(118, 86, 66, 0.08);
  color: #6e594d;
  font-weight: 600;
  border: 1rpx solid rgba(118, 86, 66, 0.08);
}

.section-no-margin {
  margin-bottom: 0;
}

.form-tip,
.list-tip {
  color: #7c675c;
  font-size: 24rpx;
  line-height: 1.7;
  margin-top: 10rpx;
}

.picker {
  height: 80rpx;
  line-height: 80rpx;
  padding: 0 24rpx;
  background: linear-gradient(180deg, rgba(255, 250, 244, 0.98), rgba(250, 238, 225, 0.98));
  border-radius: 20rpx;
  color: #584840;
  border: 1rpx solid rgba(148, 118, 91, 0.14);
}

.input {
  height: 80rpx;
  padding: 0 24rpx;
  background: linear-gradient(180deg, rgba(255, 250, 244, 0.98), rgba(250, 238, 225, 0.98));
  border-radius: 20rpx;
  color: #584840;
  border: 1rpx solid rgba(148, 118, 91, 0.14);
}

.list-card,
.history-card {
  margin-top: 24rpx;
}

.history-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.history-link {
  flex-shrink: 0;
  color: #7a4d46;
  font-size: 24rpx;
  font-weight: 600;
  line-height: 1.8;
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
  font-size: 30rpx;
  font-weight: 700;
  color: #2c201b;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.food-meta {
  margin-top: 8rpx;
  color: #7b675c;
  font-size: 24rpx;
}

.food-actions {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-shrink: 0;
}

.text-btn {
  padding: 8rpx 0;
  color: #6e5d54;
  font-size: 24rpx;
}

.danger {
  color: #b04f40;
}

.disabled {
  opacity: 0.6;
}

.history-dot {
  width: 18rpx;
  height: 18rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #a85a33, #e5b36e);
  box-shadow: 0 0 0 10rpx rgba(215, 165, 90, 0.12);
}
</style>
