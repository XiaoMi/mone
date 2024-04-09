<template>
  <div class="batch-box">
    <div
      class="item"
      :class="val == 'single' ? 'active' : 'not-active'"
      @click="changeActive('single')"
    >
      单次
    </div>
    <div
      class="item"
      :class="val == 'batch' ? 'active' : 'not-active'"
      @click="changeActive('batch')"
    >
      批处理
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  modelValue: {}
})
const emits = defineEmits(['update:modelValue', 'change'])
const val = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
    emits('change', val)
  }
})
const changeActive = (p) => {
  val.value = p
}
</script>

<style lang="scss" scoped>
.batch-box {
  display: flex;
  background: #f0f0f0;
  border-radius: 6px;
  padding: 3px;
  cursor: pointer;
  margin-bottom: 10px;
}
.item {
  flex: 1;
  text-align: center;
  border-radius: 5px;
  padding: 1px 0;
  font-weight: 500;
}
.item + .item {
  margin-left: 4px;
}
.active {
  background: #fff;
  color: #4d53e8;
}
.not-active:hover {
  background: #dbdbdb;
}
</style>
