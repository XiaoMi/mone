<template>
  <ul class="input-list" ref="inputsContainer">
    <Message
      v-for="(item, index) of list"
      :key="index"
      :date-time="item.dateTime"
      :text="item.text"
      :inversion="item.inversion"
      :error="item.error"
      :loading="item.loading"
    />
  </ul>
</template>

<script setup>
// import MyText from './MyText.vue'
import { Message } from '@/components/common-message'
import { computed, watch, nextTick, ref } from 'vue'
const props = defineProps({
  modelValue: {}
})
const list = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const inputsContainer = ref(null)
watch(
  () => list,
  () => {
    nextTick(() => {
      inputsContainer.value.scrollTop = inputsContainer.value.scrollHeight
    })
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss" scoped>
.input-list {
  height: 100%;
  overflow: scroll;
  padding: 10px 0;
}
</style>
