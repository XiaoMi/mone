<template>
  <div class="instances">
    <pre>{{instances}}</pre>
  </div>
</template>

<script setup lang="ts">
import type { Agent } from '@/api/agent'

defineProps<{
  instances: Array<any>
}>()
</script>

<style scoped>
.instances {
  max-width: 400px;
}
.instances pre {
  margin: 0;
  padding: 0;
  width: 100%;
  overflow: auto;
  white-space: pre;
  max-height: 80vh;
}
.instances pre::-webkit-scrollbar {
  display: none;
}
</style>
