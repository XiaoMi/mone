<template>
  <p class="desc">
    {{ props.desc }}
    <slot />
  </p>
</template>

<script setup>
const props = defineProps({
  desc: {
    default: '暂无描述'
  }
})
</script>

<style lang="scss" scoped>
.desc {
  color: rgba(28, 29, 35, 0.6);
  max-width: 300px;
  padding: 5px 0;
  text-align: left;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
