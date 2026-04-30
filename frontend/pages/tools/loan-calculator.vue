<template>
  <view class="page loan-page">
    <view class="card loan-hero">
      <view class="hero-topline">
        <view class="section-label">
          <brand-icon name="home" :size="24" />
          <text>生活工具</text>
        </view>
        <view class="hero-note">{{ methodLabel }}</view>
      </view>
      <view class="hero-title">贷款计算器</view>
      <view class="hero-subtitle">输入贷款总额、年利率和期限，先把月供、总利息和还款总额看清楚。</view>
      <view class="loan-result-card">
        <view>
          <view class="result-label">首月月供</view>
          <view class="result-value">¥{{ formatMoney(result.firstMonthlyPayment) }}</view>
        </view>
        <view class="result-side">
          <view class="result-label">总利息</view>
          <view class="result-side-value">¥{{ formatMoney(result.totalInterest) }}</view>
        </view>
      </view>
    </view>

    <view class="card loan-card">
      <view class="section-label">
        <brand-icon name="target" :size="24" />
        <text>贷款信息</text>
      </view>
      <view class="method-switch">
        <view
          v-for="method in repaymentMethods"
          :key="method.value"
          class="method-pill"
          :class="{ active: form.repaymentMethod === method.value }"
          @tap="selectMethod(method.value)"
        >
          {{ method.label }}
        </view>
      </view>
      <view class="field-list">
        <view class="field-row" v-for="field in fields" :key="field.key">
          <view>
            <view class="field-label">{{ field.label }}</view>
            <view class="field-desc">{{ field.desc }}</view>
          </view>
          <view class="amount-wrap">
            <input v-model="form[field.key]" class="amount-input" type="digit" placeholder="0" />
            <text class="amount-unit">{{ field.unit }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="card loan-card">
      <view class="section-label">
        <brand-icon name="stats" :size="24" />
        <text>计算结果</text>
      </view>
      <view class="summary-grid">
        <view class="summary-item">
          <view class="summary-label">首月月供</view>
          <view class="summary-value">¥{{ formatMoney(result.firstMonthlyPayment) }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-label">每月递减</view>
          <view class="summary-value">¥{{ formatMoney(result.monthlyDecrease) }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-label">总利息</view>
          <view class="summary-value">¥{{ formatMoney(result.totalInterest) }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-label">还款总额</view>
          <view class="summary-value">¥{{ formatMoney(result.totalRepayment) }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-label">贷款期数</view>
          <view class="summary-value">{{ result.months }} 期</view>
        </view>
        <view class="summary-item">
          <view class="summary-label">平均月供</view>
          <view class="summary-value">¥{{ formatMoney(result.averageMonthlyPayment) }}</view>
        </view>
      </view>
      <view class="loan-note">结果仅用于估算，实际还款以银行合同、放款时间和还款规则为准。</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { calculateLoan } from '../../utils/loan-calculator'

export default {
  components: {
    BrandIcon
  },
  data() {
    return {
      form: {
        amountWan: '',
        annualRate: '',
        years: '',
        repaymentMethod: 'equalPayment'
      },
      repaymentMethods: [
        { label: '等额本息', value: 'equalPayment' },
        { label: '等额本金', value: 'equalPrincipal' }
      ],
      fields: [
        { key: 'amountWan', label: '贷款总额', desc: '按万元填写，例如 100。', unit: '万元' },
        { key: 'annualRate', label: '年利率', desc: '按百分比填写，例如 3.6。', unit: '%' },
        { key: 'years', label: '贷款期限', desc: '按年填写，最长按 40 年估算。', unit: '年' }
      ]
    }
  },
  computed: {
    result() {
      return calculateLoan(this.form)
    },
    methodLabel() {
      const method = this.repaymentMethods.find((item) => item.value === this.form.repaymentMethod)
      return method ? method.label : '等额本息'
    }
  },
  methods: {
    selectMethod(value) {
      this.form.repaymentMethod = value
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>
.loan-hero {
  background: linear-gradient(135deg, #3f5968, #627f8d 58%, #d3b474);
  color: #fffaf2;
}

.hero-topline {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.hero-note {
  color: rgba(255, 248, 236, 0.84);
  font-size: 22rpx;
}

.hero-title {
  margin-top: 20rpx;
  font-size: 52rpx;
  line-height: 1.2;
  font-weight: 700;
  font-family: 'Iowan Old Style', 'Songti SC', 'Noto Serif SC', serif;
}

.hero-subtitle {
  margin-top: 16rpx;
  color: rgba(255, 248, 236, 0.9);
  font-size: 26rpx;
  line-height: 1.72;
}

.loan-result-card {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
  margin-top: 26rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  background: rgba(255, 248, 241, 0.15);
  border: 1rpx solid rgba(255, 245, 236, 0.16);
}

.result-label,
.summary-label {
  color: rgba(42, 32, 25, 0.68);
  font-size: 22rpx;
}

.loan-result-card .result-label {
  color: rgba(255, 248, 236, 0.82);
}

.result-value {
  margin-top: 10rpx;
  font-size: 46rpx;
  font-weight: 800;
  line-height: 1.2;
}

.result-side {
  min-width: 210rpx;
  text-align: right;
}

.result-side-value {
  margin-top: 12rpx;
  font-size: 30rpx;
  font-weight: 700;
}

.loan-card {
  margin-top: 24rpx;
}

.method-switch {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14rpx;
  margin-top: 22rpx;
  padding: 8rpx;
  border-radius: 22rpx;
  background: rgba(74, 94, 104, 0.08);
}

.method-pill {
  padding: 18rpx;
  border-radius: 18rpx;
  color: #75675f;
  font-size: 26rpx;
  text-align: center;
  font-weight: 700;
}

.method-pill.active {
  background: #fffaf4;
  color: #2c201b;
  box-shadow: 0 12rpx 28rpx rgba(74, 65, 55, 0.08);
}

.field-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  margin-top: 22rpx;
}

.field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  padding: 20rpx;
  border-radius: 22rpx;
  background: linear-gradient(180deg, rgba(255, 252, 248, 0.98), rgba(247, 250, 252, 0.96));
}

.field-label {
  color: #2c201b;
  font-size: 26rpx;
  font-weight: 700;
}

.field-desc {
  margin-top: 8rpx;
  color: #817067;
  font-size: 22rpx;
  line-height: 1.5;
}

.amount-wrap {
  display: flex;
  align-items: center;
  width: 230rpx;
  height: 74rpx;
  padding: 0 18rpx;
  border-radius: 18rpx;
  background: #fffaf4;
  box-sizing: border-box;
}

.amount-input {
  flex: 1;
  min-width: 0;
  color: #2c201b;
  font-size: 26rpx;
  text-align: right;
}

.amount-unit {
  margin-left: 8rpx;
  color: #817067;
  font-size: 22rpx;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
  margin-top: 22rpx;
}

.summary-item {
  padding: 20rpx;
  border-radius: 22rpx;
  background: linear-gradient(180deg, rgba(252, 250, 245, 0.98), rgba(244, 249, 251, 0.96));
}

.summary-value {
  margin-top: 12rpx;
  color: #2c201b;
  font-size: 30rpx;
  font-weight: 800;
  line-height: 1.28;
}

.loan-note {
  margin-top: 20rpx;
  color: #806f65;
  font-size: 22rpx;
  line-height: 1.6;
}

@media screen and (max-width: 380px) {
  .loan-result-card,
  .field-row {
    flex-direction: column;
    align-items: stretch;
  }

  .result-side {
    min-width: 0;
    text-align: left;
  }

  .amount-wrap {
    width: 100%;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
