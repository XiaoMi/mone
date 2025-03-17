<template>
  <json-viewer :value="modelValue" :expanded="true" :expand-depth="2" />
</template>

<script setup>
import { reactive, ref } from 'vue'
const props = defineProps({
  modelValue: { default: {} }
})
</script>

<style></style>
