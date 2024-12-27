<template>
  <div class="chat-list" ref="container">
    <ul>
      <Message
        v-for="(item, index) of data"
        :key="index"
        :date-time="item.dateTime"
        :text="item.text"
        :inversion="item.inversion"
        :error="item.error"
        :loading="item.loading"
        :textType="item.textType"
        :translateData="item.translateData"
        @replaceFn="replaceFn"
      />
    </ul>
  </div>
</template>

<script setup lang="ts">
import { nextTick, watch, ref } from 'vue'
const emit = defineEmits(['replaceFn'])
const props = defineProps({
  data: {
    type: Array,
    required: true
  }
})
const replaceFn = (text) => {
  emit('replaceFn', text)
}
const container = ref(null)
watch(
  () => props.data,
  () => {
    nextTick(() => {
      container.value.scrollTop = container.value.scrollHeight
    })
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.chat-list {
  height: 100%;
  padding-top: 20px;
  overflow: auto;
}
</style>
