<!--
 * @Description: 
 * @Date: 2024-01-25 15:53:27
 * @LastEditTime: 2024-01-25 17:41:16
-->
<template>
  <ul class="lang-text">
    <li
      v-for="(item, index) in menuData"
      :key="index"
      :class="active === index ? 'active' : ''"
      @click="selected(index)"
    >
      <div>{{ t('' + item.value + '') }}</div>
    </li>
  </ul>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { t } from '@/locales'

const props = defineProps({
  modelValue: {
    type: Array,
    default: []
  },
  menuData: Array<{
    value: string
  }>
})
const emits = defineEmits(['update:modelValue', 'selected'])

const active = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const selected = (index: number) => {
  active.value = index
  emits('selected', index)
}
</script>

<style scoped lang="scss">
.lang-text {
  display: flex;
  padding-right: 10px;
  font-size: 14px;
  li {
    cursor: pointer;
    height: 40px;
    line-height: 40px;
    padding: 0 20px;
    &.active {
      color: var(--oz-menu-active-color);
      border-bottom: 2px solid var(--oz-menu-active-color);
    }
    &:hover {
      background-color: var(--oz-menu-hover-bg-color);
    }
  }
}
</style>
