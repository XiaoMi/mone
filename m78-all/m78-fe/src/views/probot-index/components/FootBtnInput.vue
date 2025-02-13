<!--
 * @Description: 
 * @Date: 2024-08-15 20:05:31
 * @LastEditTime: 2024-08-23 11:34:42
-->
<template>
  <div class="foot-btn-input">
    <div class="head">
      <slot name="top" v-if="slots?.top"></slot>
      <div class="title" v-else>{{ data.title }}</div>
      <el-icon class="close" @click="close"><Close /></el-icon>
    </div>
    <div class="main">
      <BaseGroup
        :title="data?.desc"
        :tooltip="data?.tooltip"
        :showRequire="data?.desc ? true : false"
      >
        <CommmonTextarea
          ref="inputRef"
          v-model="prompt"
          class="flex-1 probot-textarea"
          @enterFn="handleSubmit"
          :placeholder="placeholder"
          type="simple"
        ></CommmonTextarea>
      </BaseGroup>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, useSlots } from 'vue'
import CommmonTextarea from '@/components/CommmonTextarea.vue'
import BaseGroup from '@/views/probot-create/components/BaseGroup.vue'

const slots = useSlots()

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: () => {}
  }
})
const emits = defineEmits(['update:modelValue', 'update'])
const visible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const prompt = ref<string>('')
const placeholder = ref('')

const handleSubmit = () => {
  emits('update', prompt.value)
  prompt.value = ''
  if (props?.data?.isClose) {
    close()
  }1
}
const close = () => {
  visible.value = false
}
</script>

<style scoped lang="scss">
.foot-btn-input {
  border: 1px solid var(--oz-menu-active-color);
  border-radius: 20px;
  overflow: hidden;
  .head {
    background-color: #f1f1f1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px;
    .title {
      padding-left: 4px;
      font-size: 16px;
      color: #333;
    }
    .close {
      color: #666;
      cursor: pointer;
      transition: all 0.3;
      font-size: 14px;
      &:hover {
        transform: scale(1.1);
        color: #333;
      }
    }
  }
  .main {
    padding: 20px;
    color: #666;
    background-color: #fff;
  }
}
</style>
