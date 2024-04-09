<template>
  <div class="outputs" v-for="item in showArr" :key="item.name">
    <div class="flex-class" v-if="typeof item.value == 'string'">
      <span class="name-i">{{ item.name }}：</span>
      <JsonViewer :value="item.value" theme="jv-light" class="json-box out-text" />
    </div>
    <div class="flex-class" v-else>
      <span class="name-i">{{ item.name }}：</span>
      <JsonViewer :value="item.value[activeNum - 1]" theme="jv-light" class="json-box out-text" />
    </div>
  </div>
</template>

<script setup>
// import { computed } from 'vue'
const props = defineProps({
  showArr: {},
  activeNum: {}
})
</script>

<style lang="scss" scoped>
.flex-class {
  display: flex;
}
.json-box {
  flex: 1;
}
</style>
