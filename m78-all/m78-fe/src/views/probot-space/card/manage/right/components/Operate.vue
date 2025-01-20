<!--
 * @Description: 
 * @Date: 2024-09-19 16:03:00
 * @LastEditTime: 2024-10-17 11:16:02
-->
<template>
  <el-form :model="form" ref="formRef">
    <FormItem title="点击事件">
      <template #topRight>
        <FormItem prop="enableClickEvent">
          <el-switch
            v-model="form.enableClickEvent"
            :active-value="1"
            :inactive-value="0"
            size="small"
          />
        </FormItem>
      </template>
      <el-select
        v-model="form.clickEventType"
        placeholder=""
        style="width: 100%"
        v-if="form.enableClickEvent"
      >
        <el-option v-for="(item, key) in options" :key="key" :label="item" :value="key" />
      </el-select>
    </FormItem>
    <template v-if="form.enableClickEvent">
      <FormItem
        title="URL"
        prop="clickUrl"
        :require="true"
        v-if="form.clickEventType == 'OPEN_URL'"
      >
        <el-input v-model="form.clickUrl" placeholder="" />
      </FormItem>
      <FormItem
        title="消息"
        prop="message"
        :require="true"
        v-else-if="form.clickEventType == 'SEND_MSG_TO_BOT'"
      >
        <el-input v-model="form.message" placeholder="" />
      </FormItem>
    </template>
  </el-form>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import FormItem from './FormItem.vue'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  }
})

const emits = defineEmits(['update:modelValue'])

const form = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const options = ref({})
onMounted(async () => {
  options.value = await cardStore.getClickEventTypeOptions()
})
</script>

<style scoped></style>
