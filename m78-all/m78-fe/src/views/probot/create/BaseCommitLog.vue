<!--
 * @Description: 
 * @Date: 2024-03-05 10:17:41
 * @LastEditTime: 2024-03-14 19:42:26
-->
<template>
  <div class="canvas-wrap">
    <calendar-heatmap
      style="width: 100%; max-width: 1200px"
      :end-date="endDate"
      :values="timeValue"
      :round="2"
      :dark-mode="false"
      :tooltip-formatter="handleFormatter"
      :tooltip="true"
      tooltip-unit="change(s)"
      :no-data-text="false"
      @onDayClick="handleClick"
      :locale="isEn ? localeEn : locale"
      :range-color="['#ebedf0', '#ebedf0', '#c6e2ff', '#a0cfff', '#79bbff', '#409EFF', '#337ecc']"
    />
  </div>
</template>
<script lang="ts" setup>
import { ref, watch, nextTick } from 'vue'
import { CalendarHeatmap, CalendarItem } from 'vue3-calendar-heatmap'
import moment from 'moment'
import { useAppStore } from '@/stores'
import dateFormat from 'dateformat'

const props = defineProps({
  data: {
    type: Object,
    default: () => []
  }
})
const appStore = useAppStore()
const isEn = ref(false)
watch(
  () => appStore.language,
  (val) => {
    nextTick(() => {
      if (val === 'en-US') {
        isEn.value = true
      } else if (val === 'zh-CN') {
        isEn.value = false
      }
    })
  },
  {
    immediate: true,
    deep: true
  }
)
interface IDATA_ITEM {
  date: string
  count: number
}

const locale = {
  months: [
    '一月',
    '二月',
    '三月',
    '四月',
    '五月',
    '六月',
    '七月',
    '八月',
    '九月',
    '十月',
    '十一月',
    '十二月'
  ],
  days: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
  less: 'less',
  more: 'more'
}

const localeEn = {
  months: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
  days: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
  less: 'less',
  more: 'more'
}

let endDate = moment().format('YYYY-MM-DD')

let timeValue: Array<IDATA_ITEM> = []

const handleFormatter = (v: CalendarItem, unit: string): string => {
  let week = ''
  switch (moment(v.date).format('e')) {
    case '0':
      week = isEn.value ? 'Sun' : '星期日'
      break
    case '1':
      week = isEn.value ? 'Mon' : '星期一'
      break
    case '2':
      week = isEn.value ? 'Tue' : '星期二'
      break
    case '3':
      week = isEn.value ? 'Wed' : '星期三'
      break
    case '4':
      week = isEn.value ? 'Thu' : '星期四'
      break
    case '5':
      week = isEn.value ? 'Fri' : '星期五'
      break
    case '6':
      week = isEn.value ? 'Sat' : '星期六'
      break
    default:
      break
  }
  return `${moment(v.date).format('YYYY年MM月DD日')} ${week} 发布${v.count}次`
}

const handleClick = (arg): void => {
  console.log(arg)
}

watch(
  () => props.data,
  (val) => {
    timeValue = (val || []).map((v: { publishTime: string }) => ({
      date: dateFormat(v.publishTime, 'yyyy-mm-dd HH:MM:ss'),
      count: 1
    }))
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss" scoped>
.canvas-wrap {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  font-size: 8px;
  padding-left: 24px;
  &:deep(.vch__legend) {
    // display: none;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    user-select: none;
    .vch__external-legend-wrapper {
      margin: 0 6px;
      width: 100px;
    }
  }
}
</style>
