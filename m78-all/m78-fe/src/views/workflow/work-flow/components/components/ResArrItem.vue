<template>
  <span class="name-i font-14">{{ item.name }}</span>
  <p class="text-p p-l-15px" v-for="(itemV, index) in item.value" :key="index">
    <span class="name-i">{{ index }}：</span>
    <span class="out-text">
      <JsonViewer v-if="typeof itemV == 'object'" :value="itemV" theme="jv-light" />
      <i v-else> {{ item.value }}</i>
    </span>
  </p>
</template>

<script setup>
const props = defineProps({
  item: {}
})
</script>

<style lang="scss" scoped>
.p-l-15px {
  padding-left: 15px;
}
.font-14 {
  font-size: 14px;
  font-weight: 500;
}
</style>
