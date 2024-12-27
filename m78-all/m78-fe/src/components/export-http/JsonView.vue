<template>
  <json-viewer :value="props.modelValue" :expanded="true" :expand-depth="2" />
</template>

<script setup>
const props = defineProps({
  modelValue: { default: {} }
})
</script>

<style></style>
