<template>
  <h1 class="title-first">{{ title }}</h1>
</template>

<script setup lang="ts">
const props = defineProps({
  title: {
    type: String,
    default: ''
  }
})
</script>

<style scoped lang="scss">
.title-first {
  color: #383743;
  font-size: 16px;
  margin-bottom: 4px;
}
</style>
