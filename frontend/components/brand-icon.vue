<template>
  <image class="brand-icon" :src="src" :style="iconStyle" :mode="mode" />
</template>

<script>
import boardActive from '../static/brand-icons/board-active.png'
import boardDefault from '../static/brand-icons/board-default.png'
import calendarActive from '../static/brand-icons/calendar-active.png'
import calendarDefault from '../static/brand-icons/calendar-default.png'
import checkinActive from '../static/brand-icons/checkin-active.png'
import checkinDefault from '../static/brand-icons/checkin-default.png'
import editActive from '../static/brand-icons/edit-active.png'
import editDefault from '../static/brand-icons/edit-default.png'
import filterActive from '../static/brand-icons/filter-active.png'
import filterDefault from '../static/brand-icons/filter-default.png'
import foodActive from '../static/brand-icons/food-active.png'
import foodDefault from '../static/brand-icons/food-default.png'
import historyActive from '../static/brand-icons/history-active.png'
import historyDefault from '../static/brand-icons/history-default.png'
import homeActive from '../static/brand-icons/home-active.png'
import homeDefault from '../static/brand-icons/home-default.png'
import keyActive from '../static/brand-icons/key-active.png'
import keyDefault from '../static/brand-icons/key-default.png'
import listActive from '../static/brand-icons/list-active.png'
import listDefault from '../static/brand-icons/list-default.png'
import mineActive from '../static/brand-icons/mine-active.png'
import mineDefault from '../static/brand-icons/mine-default.png'
import sparkActive from '../static/brand-icons/spark-active.png'
import sparkDefault from '../static/brand-icons/spark-default.png'
import statsActive from '../static/brand-icons/stats-active.png'
import statsDefault from '../static/brand-icons/stats-default.png'
import wakeActive from '../static/brand-icons/wake-active.png'
import wakeDefault from '../static/brand-icons/wake-default.png'

const iconRegistry = {
  home: {
    default: homeDefault,
    active: homeActive
  },
  food: {
    default: foodDefault,
    active: foodActive
  },
  checkin: {
    default: checkinDefault,
    active: checkinActive
  },
  mine: {
    default: mineDefault,
    active: mineActive
  },
  spark: {
    default: sparkDefault,
    active: sparkActive
  },
  filter: {
    default: filterDefault,
    active: filterActive
  },
  edit: {
    default: editDefault,
    active: editActive
  },
  list: {
    default: listDefault,
    active: listActive
  },
  history: {
    default: historyDefault,
    active: historyActive
  },
  stats: {
    default: statsDefault,
    active: statsActive
  },
  board: {
    default: boardDefault,
    active: boardActive
  },
  calendar: {
    default: calendarDefault,
    active: calendarActive
  },
  key: {
    default: keyDefault,
    active: keyActive
  },
  wake: {
    default: wakeDefault,
    active: wakeActive
  }
}

export default {
  name: 'BrandIcon',
  props: {
    name: {
      type: String,
      required: true
    },
    tone: {
      type: String,
      default: 'active'
    },
    size: {
      type: [Number, String],
      default: 40
    },
    mode: {
      type: String,
      default: 'aspectFit'
    }
  },
  computed: {
    src() {
      const entry = iconRegistry[this.name]
      if (!entry) {
        return iconRegistry.spark.active
      }

      return entry[this.tone] || entry.active
    },
    iconStyle() {
      const sizeValue = typeof this.size === 'number' ? `${this.size}rpx` : this.size
      return `width:${sizeValue};height:${sizeValue};`
    }
  }
}
</script>

<style scoped>
.brand-icon {
  display: block;
  flex-shrink: 0;
}
</style>
