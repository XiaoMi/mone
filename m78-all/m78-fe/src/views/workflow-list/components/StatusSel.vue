<template>
  <el-select v-model="val" style="width: 90px">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
    </el-option>
  </el-select>
</template>

<script setup>
import { ref, computed } from 'vue'
const props = defineProps({
  modelVal: {}
})
const emits = defineEmits(['update:modelVal'])
const val = computed({
  get() {
    return props.modelVal
  },
  set(v) {
    emits('update:modelVal', v)
  }
})
const options = ref([
  { label: '全部', value: 'all' },
  {
    label: '已发布',
    value: 'published'
  },
  {
    label: '未发布',
    value: 'unPublished'
  }
])
</script>

<style lang="scss" scoped></style>
