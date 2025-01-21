<template>
  <ResItemVal :item="item" />
</template>

<script setup>
import ResItemVal from '@/views/workflow/work-flow/components/components/ResItemVal.vue'
const props = defineProps({
  item: {}
})
</script>

<style lang="scss" scoped></style>
