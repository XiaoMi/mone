<template>
  <p class="text">
    <span class="text-span bubble-border">
      {{ text }}
    </span>
  </p>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
  text: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.text-span {
  display: inline-block;
  padding: 4px 10px;
}
.bubble-border {
  border-radius: 3px;
  background-color: #ffffff;
  border: 1px solid #000;
  position: relative;
  margin-left: 7px;
}
.bubble-border::before,
.bubble-border::after {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  left: -7px;
  content: '';
  width: 0;
  height: 0;
  border-width: 6px;
  border-style: solid;
  border-color: transparent #000 transparent transparent;
}
.bubble-border::after {
  left: -6px;
  border-right-color: #ffffff;
}
.text {
  margin-top: 10px;
}
</style>
