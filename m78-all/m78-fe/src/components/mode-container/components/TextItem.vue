<template>
  <div class="div600">{{ text }}</div>
</template>

<script setup lang="ts">
const props = defineProps({
  text: {}
})
</script>

<style scoped>
.div600 {
  width: 600px;
  height: 600px;
  border: dashed 1px #dcdfe6;
  padding: 10px;
  font-size: 16px;
  line-height: 25px;
  overflow-y: auto;
  /* word-break: break-all; */
}
</style>
