<template>
  <p class="desc">{{ props.desc }}</p>
</template>

<script setup>
const props = defineProps({
  desc: {
    default: '描述'
  }
})
</script>

<style lang="scss" scoped>
.desc {
  color: rgba(28, 29, 35, 0.6);
  padding: 5px 0;
  text-align: left;
}
</style>
