<!--
 * @Description: 
 * @Date: 2024-07-25 14:30:17
 * @LastEditTime: 2024-07-26 20:46:23
-->
<template>
  <FormItem :title="title" :prop="prop">
    <el-button
      plain
      v-for="(item, index) in options"
      :key="index"
      :type="value === item.width + props.separator + item.height ? 'primary' : ''"
      @click="value = item.width + props.separator + item.height"
    >
      <span
        class="size-frame"
        :style="
          'width: ' +
          Math.max(item.width > 20 ? item.width / 100 : item.width, 10) +
          'px;height:' +
          Math.max(item.height > 20 ? item.height / 100 : item.height, 10) +
          'px'
        "
      ></span>
      <span>{{ item.width + props.separator + item.height }}</span>
    </el-button>
  </FormItem>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import FormItem from './FormItem.vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  prop: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: '分辨率'
  },
  separator: {
    type: String,
    default: '*'
  },
  options: {
    type: Array<{
      width: string | number
      height: string | number
    }>,
    default: [
      {
        width: '1024',
        height: '1024'
      },
      {
        width: '720',
        height: '1280'
      },
      {
        width: '1280',
        height: '720'
      }
    ]
  }
})
const emits = defineEmits(['update:modelValue'])

const value = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>

<style scoped>
.size-frame {
  display: inline-block;
  border: 1px solid #606266;
  margin-right: 2px;
}
.oz-button:hover .size-frame {
  border-color: var(--oz-color-primary);
}
.oz-button--primary:hover .size-frame {
  border-color: #fff;
}
.oz-button--primary .size-frame {
  border-color: var(--oz-color-primary);
}
</style>
