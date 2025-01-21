<template>
  <CollapseIcon :activeNames="activeNames" />
  <TitleTooltip
    :title="title"
    :content="content"
    :class="tipClass"
    :showAdd="showAdd"
    raw-content
    @add="addFn"
  />
</template>

<script setup>
import CollapseIcon from './CollapseIcon.vue'
import TitleTooltip from '@/views/workflow/work-flow/components/TitleTooltip'
import { defineEmits } from 'vue'

const props = defineProps({
  activeNames: {},
  title: {},
  content: {},
  showAdd: {},
  tipClass: {}
})
const emits = defineEmits(['add'])
const addFn = () => {
  if (!props.showAdd) return
  emits('add')
}
</script>

<style lang="scss" scoped></style>
