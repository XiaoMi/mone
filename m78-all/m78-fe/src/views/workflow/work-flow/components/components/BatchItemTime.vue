<template>
  <div
    class="used-time"
    v-if="
      batchItem.find((it) => it.name === 'outputList')?.value[props.indexNum]?.$$TY_ELAPSED_TIME$$
    "
  >
    <i>耗时：</i>
    <i>{{ timer }}</i>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { formatTime } from '../../baseInfo.js'

const props = defineProps({
  batchItem: {},
  indexNum: {}
})
const timer = computed(() => {
  return formatTime(
    props.batchItem.find((it) => it.name === 'outputList')?.value[props.indexNum]
      ?.$$TY_ELAPSED_TIME$$
  )
})
</script>

<style lang="scss" scoped>
.used-time {
  height: 34px;
  line-height: 34px;
  background-color: #fff;
  border-radius: 8px;
  padding: 0 10px;
  font-size: 13px;
  font-weight: 500;
  color: var(--oz-collapse-header-text-color);
  margin-bottom: 10px;
}
</style>
