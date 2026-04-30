<template>
  <image class="brand-icon" :src="src" :style="iconStyle" :mode="mode" />
</template>

<script>
import addActive from '../static/brand-icons/add-active.svg'
import addDefault from '../static/brand-icons/add-default.svg'
import boardActive from '../static/brand-icons/board-active.png'
import boardDefault from '../static/brand-icons/board-default.png'
import calendarActive from '../static/brand-icons/calendar-active.png'
import calendarDefault from '../static/brand-icons/calendar-default.png'
import checkinActive from '../static/brand-icons/checkin-active.png'
import checkinDefault from '../static/brand-icons/checkin-default.png'
import deleteActive from '../static/brand-icons/delete-active.svg'
import deleteDefault from '../static/brand-icons/delete-default.svg'
import editActive from '../static/brand-icons/edit-active.png'
import editDefault from '../static/brand-icons/edit-default.png'
import emptyActive from '../static/brand-icons/empty-active.svg'
import emptyDefault from '../static/brand-icons/empty-default.svg'
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
import logoutActive from '../static/brand-icons/logout-active.svg'
import logoutDefault from '../static/brand-icons/logout-default.svg'
import mineActive from '../static/brand-icons/mine-active.png'
import mineDefault from '../static/brand-icons/mine-default.png'
import notificationActive from '../static/brand-icons/notification-active.svg'
import notificationDefault from '../static/brand-icons/notification-default.svg'
import petalActive from '../static/brand-icons/petal-active.svg'
import petalDefault from '../static/brand-icons/petal-default.svg'
import profileActive from '../static/brand-icons/profile-active.svg'
import profileDefault from '../static/brand-icons/profile-default.svg'
import quoteActive from '../static/brand-icons/quote-active.svg'
import quoteDefault from '../static/brand-icons/quote-default.svg'
import saveActive from '../static/brand-icons/save-active.svg'
import saveDefault from '../static/brand-icons/save-default.svg'
import sparkActive from '../static/brand-icons/spark-active.png'
import sparkDefault from '../static/brand-icons/spark-default.png'
import statsActive from '../static/brand-icons/stats-active.png'
import statsDefault from '../static/brand-icons/stats-default.png'
import successActive from '../static/brand-icons/success-active.svg'
import successDefault from '../static/brand-icons/success-default.svg'
import targetActive from '../static/brand-icons/target-active.svg'
import targetDefault from '../static/brand-icons/target-default.svg'
import teaActive from '../static/brand-icons/tea-active.svg'
import teaDefault from '../static/brand-icons/tea-default.svg'
import timeActive from '../static/brand-icons/time-active.svg'
import timeDefault from '../static/brand-icons/time-default.svg'
import warningActive from '../static/brand-icons/warning-active.svg'
import warningDefault from '../static/brand-icons/warning-default.svg'
import wakeActive from '../static/brand-icons/wake-active.png'
import wakeDefault from '../static/brand-icons/wake-default.png'

const iconRegistry = {
  add: {
    default: addDefault,
    active: addActive
  },
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
  delete: {
    default: deleteDefault,
    active: deleteActive
  },
  list: {
    default: listDefault,
    active: listActive
  },
  save: {
    default: saveDefault,
    active: saveActive
  },
  history: {
    default: historyDefault,
    active: historyActive
  },
  empty: {
    default: emptyDefault,
    active: emptyActive
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
  logout: {
    default: logoutDefault,
    active: logoutActive
  },
  wake: {
    default: wakeDefault,
    active: wakeActive
  },
  quote: {
    default: quoteDefault,
    active: quoteActive
  },
  target: {
    default: targetDefault,
    active: targetActive
  },
  notification: {
    default: notificationDefault,
    active: notificationActive
  },
  profile: {
    default: profileDefault,
    active: profileActive
  },
  time: {
    default: timeDefault,
    active: timeActive
  },
  success: {
    default: successDefault,
    active: successActive
  },
  warning: {
    default: warningDefault,
    active: warningActive
  },
  tea: {
    default: teaDefault,
    active: teaActive
  },
  petal: {
    default: petalDefault,
    active: petalActive
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
