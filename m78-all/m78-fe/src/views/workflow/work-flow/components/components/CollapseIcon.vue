<template>
  <el-icon>
    <ArrowDown v-if="activeNames.includes('1')" />
    <ArrowRight v-else />
  </el-icon>
</template>

<script setup>
const props = defineProps({
  activeNames: {
    type: Array,
    default: () => []
  }
})
</script>

<style lang="scss" scoped></style>
