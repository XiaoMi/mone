<template>
  <div class="result-pop">
    <slot name="reference"></slot>
    <div
      class="pop-box nowheel"
      v-show="visible"
      :style="{ width: width + 'px', right: -Math.abs(props.width + 25) + 'px' }"
    >
      <p class="t">{{ title }}</p>
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  visible: {},
  width: {
    type: Number,
    default: 500
  },
  title: {
    type: String,
    default: '结果'
  }
})
const rightVal = computed(() => {
  return -Math.abs(props.width + 25)
})
</script>

<style lang="scss" scoped>
.result-pop {
  position: relative;
}
.pop-box {
  position: absolute;
  border: solid 1px #e4e7ed;
  box-shadow:
    0 0 1px 0 rgba(0, 0, 0, 0.3),
    0 4px 68px 3px rgba(0, 0, 0, 0.14);
  background: #f7f7fa;
  padding: 12px;
  z-index: 2000;
  top: -14px;
  border-radius: 8px;
  min-width: 150px;
  max-height: 700px;
  overflow-y: auto;
}
.t {
  font-size: 15px;
  font-weight: 600;
  padding: 5px 0 8px 0;
}
</style>
