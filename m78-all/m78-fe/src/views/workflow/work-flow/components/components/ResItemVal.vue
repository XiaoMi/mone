<template>
  <span class="out-text">
    <JsonViewer v-if="typeof item == 'object'" :value="item" sort theme="jv-light" :key="item" />
    <template v-else>
      <pre><code class="code-class">{{ str }}</code></pre>
    </template>
    <slot></slot>
  </span>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  item: {}
})
const str = computed(() => {
  if (typeof props.item != 'object') {
    const str = `${props.item}` || ''
    return str
  } else {
    return ''
  }
})
</script>

<style lang="scss" scoped>
.code-class {
  white-space: pre-wrap;
  // word-wrap: break-word;
}
</style>
