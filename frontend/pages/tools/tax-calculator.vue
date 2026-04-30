<template>
  <view class="page tax-page">
    <view class="card tax-hero">
      <view class="hero-topline">
        <view class="section-label">
          <brand-icon name="stats" :size="24" />
          <text>生活工具</text>
        </view>
        <view class="hero-note">累计预扣预缴</view>
      </view>
      <view class="hero-title">个税计算器</view>
      <view class="hero-subtitle">把本月工资、扣除和上月累计数放在一起，先估一遍本月应预扣税额。</view>
      <view class="tax-result-card">
        <view>
          <view class="result-label">本月应预扣税额</view>
          <view class="result-value">¥{{ formatMoney(result.currentTax) }}</view>
        </view>
        <view class="result-side">
          <view class="result-label">税后到手</view>
          <view class="result-side-value">¥{{ formatMoney(result.afterTaxIncome) }}</view>
        </view>
      </view>
    </view>

    <view class="card tax-card">
      <view class="section-label">
        <brand-icon name="time" :size="24" />
        <text>本月信息</text>
      </view>
      <view class="field-list">
        <picker :range="monthOptions" :value="monthIndex" @change="onMonthChange">
          <view class="picker-surface">所属月份：{{ form.month }} 月</view>
        </picker>
        <view class="field-row" v-for="field in currentFields" :key="field.key">
          <view>
            <view class="field-label">{{ field.label }}</view>
            <view class="field-desc">{{ field.desc }}</view>
          </view>
          <input v-model="form[field.key]" class="amount-input" type="digit" placeholder="0.00" />
        </view>
      </view>
    </view>

    <view class="card tax-card">
      <view class="section-label">
        <brand-icon name="history" :size="24" />
        <text>上月累计</text>
      </view>
      <view class="field-list">
        <view class="field-row" v-for="field in previousFields" :key="field.key">
          <view>
            <view class="field-label">{{ field.label }}</view>
            <view class="field-desc">{{ field.desc }}</view>
          </view>
          <input v-model="form[field.key]" class="amount-input" type="digit" placeholder="0.00" />
        </view>
      </view>
    </view>

    <view class="card tax-card">
      <view class="section-label">
        <brand-icon name="target" :size="24" />
        <text>计算结果</text>
      </view>
      <view class="summary-grid">
        <view class="summary-item">
          <view class="summary-label">累计应纳税所得额</view>
          <view class="summary-value">¥{{ formatMoney(result.taxableIncome) }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-label">适用预扣率</view>
          <view class="summary-value">{{ rateText }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-label">速算扣除数</view>
          <view class="summary-value">¥{{ formatMoney(result.quickDeduction) }}</view>
        </view>
        <view class="summary-item">
          <view class="summary-label">累计应预扣税额</view>
          <view class="summary-value">¥{{ formatMoney(result.cumulativeTax) }}</view>
        </view>
      </view>
      <view class="tax-note">已按每月 5000 元减除费用和工资薪金累计预扣预缴口径估算，实际金额以单位申报和税务机关口径为准。</view>
    </view>
  </view>
</template>

<script>
import BrandIcon from '../../components/brand-icon.vue'
import { calculateMonthlyPayrollTax } from '../../utils/tax-calculator'

export default {
  components: {
    BrandIcon
  },
  data() {
    return {
      monthOptions: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      form: {
        month: 1,
        currentIncome: '',
        currentSocialSecurity: '',
        currentSpecialAdditionalDeduction: '',
        currentOtherDeduction: '',
        previousIncome: '',
        previousSocialSecurity: '',
        previousSpecialAdditionalDeduction: '',
        previousOtherDeduction: '',
        previousTaxPaid: '',
        taxReduction: ''
      },
      currentFields: [
        { key: 'currentIncome', label: '本月税前收入', desc: '工资、奖金等本月计税收入。' },
        { key: 'currentSocialSecurity', label: '本月五险一金', desc: '个人承担部分，手动填写。' },
        { key: 'currentSpecialAdditionalDeduction', label: '本月专项附加扣除', desc: '子女教育、住房租金等。' },
        { key: 'currentOtherDeduction', label: '本月其他扣除', desc: '企业年金等依法可扣除项目。' }
      ],
      previousFields: [
        { key: 'previousIncome', label: '截至上月累计收入', desc: '本年度上月已累计的税前收入。' },
        { key: 'previousSocialSecurity', label: '截至上月累计五险一金', desc: '本年度上月已累计专项扣除。' },
        { key: 'previousSpecialAdditionalDeduction', label: '截至上月累计专项附加', desc: '本年度上月已累计专项附加扣除。' },
        { key: 'previousOtherDeduction', label: '截至上月累计其他扣除', desc: '本年度上月已累计其他扣除。' },
        { key: 'previousTaxPaid', label: '截至上月已预扣税额', desc: '本年度上月已扣缴的个税。' },
        { key: 'taxReduction', label: '累计减免税额', desc: '没有可填 0。' }
      ]
    }
  },
  computed: {
    monthIndex() {
      return Math.max(Number(this.form.month) - 1, 0)
    },
    result() {
      return calculateMonthlyPayrollTax(this.form)
    },
    rateText() {
      return `${Math.round(this.result.taxRate * 100)}%`
    }
  },
  methods: {
    onMonthChange(event) {
      this.form.month = Number(event.detail.value) + 1
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>
.tax-hero {
  background: linear-gradient(135deg, #435d52, #6f8a70 58%, #d5bd7a);
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

.tax-result-card {
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

.tax-result-card .result-label {
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

.tax-card {
  margin-top: 24rpx;
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
  background: linear-gradient(180deg, rgba(255, 252, 248, 0.98), rgba(255, 247, 239, 0.96));
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

.amount-input {
  width: 220rpx;
  height: 74rpx;
  padding: 0 18rpx;
  border-radius: 18rpx;
  background: #fffaf4;
  color: #2c201b;
  font-size: 26rpx;
  text-align: right;
  box-sizing: border-box;
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
  background: linear-gradient(180deg, rgba(252, 250, 245, 0.98), rgba(245, 250, 244, 0.96));
}

.summary-value {
  margin-top: 12rpx;
  color: #2c201b;
  font-size: 30rpx;
  font-weight: 800;
  line-height: 1.28;
}

.tax-note {
  margin-top: 20rpx;
  color: #806f65;
  font-size: 22rpx;
  line-height: 1.6;
}

@media screen and (max-width: 380px) {
  .tax-result-card,
  .field-row {
    flex-direction: column;
    align-items: stretch;
  }

  .result-side {
    min-width: 0;
    text-align: left;
  }

  .amount-input {
    width: 100%;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
