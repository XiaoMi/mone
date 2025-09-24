<!--
 * @Description: 
 * @Date: 2024-10-17 15:29:26
 * @LastEditTime: 2024-10-21 19:52:36
-->
<template>
  <Container>
    <template #data>
      <FormItem prop="" title="内容" :require="true" style="width: 100%">
        <el-input
          type="textarea"
          :autosize="{ minRows: 1, maxRows: 8 }"
          v-model="property.content.value"
        ></el-input>
      </FormItem>
    </template>
    <template v-if="property.visibilitySetting" #high>
      <Visible v-model="property.visibilitySetting"></Visible>
    </template>
  </Container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Container from './Container.vue'
import FormItem from './components/FormItem.vue'
import Visible from './components/Visible.vue'

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
})
const emits = defineEmits(['update:modelValue'])
const property = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>
<style lang="scss" scoped></style>
