<template>
  <div>
    <div v-if="itemV.taskType == 0" class="box">
      {{ ymdOptions.find((item) => item.value == coreType)?.label }}
      <span v-if="coreType == 'week'">
        {{ weekDays.find((item) => item.value == itemV.taskDetail?.week)?.label }}
      </span>
      <span v-if="coreType == 'month'">
        {{ daysOps.find((item) => item.value == itemV.taskDetail?.month)?.label }}
      </span>
      {{ timer }}
    </div>
    <div v-else>-</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ymdOptions, daysOps, weekDays } from '@/components/probot/baseInfo.js'

const props = defineProps({
  itemV: {}
})
const coreType = computed(() => {
  const { taskDetail, taskType } = props.itemV
  if (!taskDetail || taskType != 0) return null
  const { month, week, day } = taskDetail
  if (month != 'null') {
    return 'month'
  } else if (week != 'null') {
    return 'week'
  } else {
    return 'day'
  }
})
const timer = computed(() => {
  const { taskDetail, taskType } = props.itemV
  if (!taskDetail || taskType != 0) return null
  const { hour, minute, second } = taskDetail
  return `${hour}:${minute}:${second}`
})
</script>

<style lang="scss" scoped>
.box {
  display: flex;
}
</style>
