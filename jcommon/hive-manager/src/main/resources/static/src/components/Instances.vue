<template>
  <div class="instances">
    <pre>{{agent.instances}}</pre>
  </div>
</template>

<script setup lang="ts">
import type { Agent } from '@/api/agent'

defineProps<{
  agent: Agent
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
  overflow-x: auto;
  white-space: pre;
}
.instances pre::-webkit-scrollbar {
  display: none;
}
</style>
