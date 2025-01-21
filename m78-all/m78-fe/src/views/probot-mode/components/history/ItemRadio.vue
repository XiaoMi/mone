<template>
  <div class="check-box" :class="checked ? 'checked-box' : ''" @click="switchChecked">
    <div class="circle" :class="checked ? 'checked-circle' : 'un-checked'">
      <i class="iconfont icon-checkbox-checked" v-if="checked"></i>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineEmits, computed } from 'vue'

const props = defineProps({
  modelValue: {}
})
const emits = defineEmits(['update:modelValue'])
const checked = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emits('update:modelValue', value)
  }
})
const switchChecked = (e) => {
  e.stopPropagation()
  checked.value = !checked.value
}
</script>

<style scoped lang="scss">
.check-box {
  position: relative;
}
.circle {
  width: 18px;
  height: 18px;
  line-height: 18px;
  border-radius: 50%;
  border: solid 0.5px #3ca9ff;
  position: absolute;
  top: 10px;
  right: 10px;
  text-align: center;
  &.checked-circle {
    background-color: rgb(255, 255, 255, 35%);
  }
  &.un-checked {
    background-color: rgba(0, 0, 0, 0.6);
  }
}

.icon-checkbox-checked {
  font-size: 12px;
  margin: 0;
  color: #3ca9ff;
}
.check-box {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  border-radius: 10px;
}
.checked-box {
  background-color: rgba(0, 0, 0, 0.5);
}
</style>
