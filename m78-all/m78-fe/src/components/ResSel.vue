<template>
  <el-select v-model="val" placeholder="插件返回内容类型" clearable>
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getResTypes } from '@/api/plugins.ts'

const options = ref([])
const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {}
})
const val = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const getOps = () => {
  getResTypes().then((res) => {
    const resArr = res.data || []
    options.value = resArr.map((item) => {
      return {
        label: item.name,
        value: item.code
      }
    })
  })
}
onMounted(() => {
  getOps()
})
</script>

<style lang="scss" scoped></style>
