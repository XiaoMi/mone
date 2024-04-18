<template>
  <el-drawer v-model="drawer" @close="close" title="调试" direction="rtl" size="50%">
    <DebuggerPage :data="data" type="debug"></DebuggerPage>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import DebuggerPage from './DebuggerPage.vue'

const props = defineProps<{
  modelValue: boolean
  data: Object
}>()

const emits = defineEmits(['update:modelValue'])

const drawer = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emits('update:modelValue', value)
  }
})

const close = () => {}
</script>
