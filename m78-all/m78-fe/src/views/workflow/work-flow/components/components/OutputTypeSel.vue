<template>
  <el-select v-model="val" placeholder="类型" style="width: 60px" :disabled="disabled">
    <el-option
      v-for="item in realOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  showImg: {
    default: false
  },
  disabled: {
    default: false
  }
})
const val = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const options = ref([
  {
    value: 'reference',
    label: '引用'
  },
  {
    value: 'imageReference',
    label: '引用'
  },
  {
    value: 'value',
    label: '输入'
  }
])
const realOptions = computed(() => {
  if (!props.showImg) {
    // 不展示图片引用
    return options.value.filter((item) => item.value != 'imageReference')
  } else {
    return options.value
  }
})
</script>

<style lang="scss" scoped></style>
